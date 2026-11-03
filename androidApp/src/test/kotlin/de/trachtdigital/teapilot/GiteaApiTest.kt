package de.trachtdigital.teapilot

import de.trachtdigital.teapilot.core.ApiResponse
import de.trachtdigital.teapilot.core.ApiTransport
import org.junit.Assert.*
import org.junit.Test

class GiteaApiTest {
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
