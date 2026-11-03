package de.trachtdigital.teapilot.core

import java.net.URI

data class ServerAddress private constructor(val value: String) {
    companion object {
        fun parse(input: String): Result<ServerAddress> = runCatching {
            val uri = URI(input.trim())
            require(uri.scheme.equals("https", ignoreCase = true)) { "HTTPS required" }
            require(!uri.host.isNullOrBlank()) { "Valid host required" }
            require(uri.rawUserInfo == null) { "Credentials are not allowed" }
            require(uri.rawQuery == null && uri.rawFragment == null) { "Query and fragment are not allowed" }
            require(uri.port == -1 || uri.port in 1..65535) { "Invalid port" }
            val port = if (uri.port == -1 || uri.port == 443) "" else ":${uri.port}"
            ServerAddress("https://${uri.host.lowercase()}$port${uri.rawPath.orEmpty().trimEnd('/')}")
        }
    }
}
