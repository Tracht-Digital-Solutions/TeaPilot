package de.trachtdigital.teapilot.core

import java.io.ByteArrayInputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.test.*

class GiteaTransportTest {
    private class Connection(url: URL, private val status: Int = 200) : HttpURLConnection(url) {
        var disconnected = false
        override fun connect() {}
        override fun disconnect() { disconnected = true }
        override fun usingProxy() = false
        override fun getResponseCode() = status
        override fun getInputStream() = ByteArrayInputStream("{}".toByteArray())
        override fun getHeaderField(name: String?) = if (name == "Link") "<https://evil.example/?token=secret>; rel=\"next\"" else null
    }

    @Test fun sendsTokenOnlyAsHeaderAndDisablesRedirects() {
        val server = ServerAddress.parse("https://git.example.org/gitea").getOrThrow()
        lateinit var connection: Connection
        val client = GiteaTransport(server) { url -> Connection(url).also { connection = it } }
        val response = client.get("/user/repos?page=1&limit=50", "test-credential")
        assertEquals("https://git.example.org/gitea/api/v1/user/repos?page=1&limit=50", connection.url.toString())
        assertEquals("token test-credential", connection.getRequestProperty("Authorization"))
        assertFalse(connection.instanceFollowRedirects)
        assertFalse(connection.useCaches)
        assertTrue(connection.disconnected)
        assertTrue(response.hasNext)
    }

    @Test fun rejectsRedirectAndDoesNotLeakServerContent() {
        val client = GiteaTransport(ServerAddress.parse("https://git.example.org").getOrThrow()) { Connection(it, 302) }
        val error = assertFailsWith<ApiFailure> { client.get("/user", "test-credential") }
        assertEquals(302, error.status)
        assertFalse(error.message.orEmpty().contains("test-credential"))
    }

    @Test fun rejectsHeaderInjectionBeforeOpeningConnection() {
        val client = GiteaTransport(ServerAddress.parse("https://git.example.org").getOrThrow()) { error("Must not connect") }
        assertFailsWith<IllegalArgumentException> { client.get("/user", "test\r\nInjected: value") }
        assertFailsWith<IllegalArgumentException> { client.get("https://evil.example/user", "test") }
    }
}
