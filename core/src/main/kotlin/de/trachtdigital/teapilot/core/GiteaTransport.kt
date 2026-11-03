package de.trachtdigital.teapilot.core

import java.io.IOException
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL

class ApiFailure(val status: Int) : IOException("Gitea request failed (HTTP $status)")

data class ApiResponse(val body: String, val hasNext: Boolean)

fun interface ApiTransport {
    fun get(path: String, token: String?): ApiResponse
}

/** HTTPS only. Never follow redirects or include server error bodies in exceptions. */
class GiteaTransport(
    private val server: ServerAddress,
    private val open: (URL) -> HttpURLConnection = { it.openConnection() as HttpURLConnection },
) : ApiTransport {
    override fun get(path: String, token: String?): ApiResponse {
        require(path.matches(Regex("/(version|user|user/repos\\?page=[1-9][0-9]*&limit=50)")))
        require(token == null || (token.isNotEmpty() && token.length <= 4096 && token.all { it.code in 33..126 }))
        val connection = open(URL("${server.value}/api/v1$path"))
        try {
            connection.instanceFollowRedirects = false
            connection.connectTimeout = 15_000
            connection.readTimeout = 15_000
            connection.useCaches = false
            connection.requestMethod = "GET"
            connection.setRequestProperty("Accept", "application/json")
            if (token != null) connection.setRequestProperty("Authorization", "token $token")
            val status = connection.responseCode
            if (status != 200) throw ApiFailure(status)
            val bytes = connection.inputStream.use { input ->
                val output = ByteArrayOutputStream()
                val buffer = ByteArray(8192)
                var total = 0
                while (true) {
                    val count = input.read(buffer)
                    if (count < 0) break
                    total += count
                    if (total > MAX_RESPONSE_BYTES) throw IOException("Response size limit exceeded")
                    output.write(buffer, 0, count)
                }
                output.toByteArray()
            }
            // Only use the relation flag. Never request server-supplied pagination URLs.
            val hasNext = connection.getHeaderField("Link").orEmpty().split(',').any {
                Regex(";\\s*rel\\s*=\\s*\"next\"").containsMatchIn(it)
            }
            return ApiResponse(bytes.toString(Charsets.UTF_8), hasNext)
        } finally {
            connection.disconnect()
        }
    }

    companion object { private const val MAX_RESPONSE_BYTES = 4 * 1024 * 1024 }
}
