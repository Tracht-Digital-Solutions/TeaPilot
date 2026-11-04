package de.trachtdigital.teapilot

import de.trachtdigital.teapilot.core.ApiResponse
import de.trachtdigital.teapilot.core.ApiTransport
import de.trachtdigital.teapilot.core.ApiFailure
import org.junit.Assert.*
import org.junit.Test

class GiteaApiTest {
    @Test fun protectedDiscoveryRetriesOnlyWithTheConfiguredSessionToken() {
        for (status in listOf(401, 403)) {
            val requests = mutableListOf<Pair<String, String?>>()
            val api = GiteaApi(ApiTransport { path, token ->
                requests += path to token
                when (path) {
                    "/version" -> {
                        if (token == null) throw ApiFailure(status)
                        ApiResponse("""{"version":"1.24.0"}""", false)
                    }
                    "/user" -> ApiResponse("""{"login":"alice"}""", false)
                    "/user/repos?page=1&limit=50" -> ApiResponse("[]", false)
                    else -> error("Unexpected endpoint")
                }
            })
            val result = api.signIn("test-credential")
            assertEquals("alice", result.username)
            assertEquals(listOf("/version" to null, "/version" to "test-credential", "/user" to "test-credential",
                "/user/repos?page=1&limit=50" to "test-credential"), requests)
        }
    }

    @Test fun discoveryFailuresDoNotTriggerAuthenticatedRetries() {
        for (status in listOf(301, 404, 429, 500)) {
            var requests = 0
            val api = GiteaApi(ApiTransport { _, token ->
                requests++
                assertNull(token)
                throw ApiFailure(status)
            })
            try {
                api.signIn("test-credential")
                fail("Expected discovery failure")
            } catch (error: ApiFailure) {
                assertEquals(status, error.status)
            }
            assertEquals(1, requests)
        }
    }

    @Test fun failedAuthenticationNeverLoadsRepositories() {
        val paths = mutableListOf<String>()
        val api = GiteaApi(ApiTransport { path, _ ->
            paths += path
            if (path == "/version") ApiResponse("""{"version":"1.24.0"}""", false)
            else throw ApiFailure(401)
        })
        try {
            api.signIn("test-credential")
            fail("Expected authentication failure")
        } catch (error: ApiFailure) {
            assertEquals(401, error.status)
        }
        assertEquals(listOf("/version", "/user"), paths)
    }

    @Test fun versionDiscoveryDoesNotSendToken() {
        val api = GiteaApi(ApiTransport { path, token ->
            assertEquals("/version", path)
            assertNull(token)
            ApiResponse("""{"version":"1.24.0"}""", false)
        })
        assertEquals("1.24.0", api.version())
    }

    @Test fun userRequiresAuthenticatedResponse() {
        val api = GiteaApi(ApiTransport { path, token ->
            assertEquals("/user", path)
            assertEquals("test-credential", token)
            ApiResponse("""{"login":"alice"}""", false)
        })
        assertEquals("alice", api.username("test-credential"))
    }

    @Test fun paginationPreservesPrivacyAndServerPageCap() {
        val api = GiteaApi(ApiTransport { path, _ ->
            assertEquals("/user/repos?page=2&limit=50", path)
            // Server may cap the requested page size below 50. Link decides.
            ApiResponse("""[{"id":7,"full_name":"team/private","description":null,"private":true}]""", true)
        })
        val result = api.repositories("test-credential", 2)
        assertTrue(result.hasNext)
        assertTrue(result.repositories.single().isPrivate)
        assertEquals("", result.repositories.single().description)
    }

    @Test fun emptyPageStopsPaginationEvenWithBadNextHeader() {
        val api = GiteaApi(ApiTransport { _, _ -> ApiResponse("[]", true) })
        assertFalse(api.repositories("test-credential", 1).hasNext)
    }

    @Test(expected = Exception::class) fun rejectsHtmlInsteadOfTreatingLoginPageAsSuccess() {
        GiteaApi(ApiTransport { _, _ -> ApiResponse("<html>sign in</html>", false) }).username("test-credential")
    }
}
