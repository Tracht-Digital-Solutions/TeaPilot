package de.trachtdigital.teapilot.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ServerAddressTest {
    @Test fun preservesSubpath() {
        assertEquals("https://git.example.org/gitea", ServerAddress.parse("https://git.example.org/gitea/").getOrThrow().value)
    }
    @Test fun rejectsCredentialsAndInsecureTransport() {
        listOf("http://git.example.org", "https://user:secret@git.example.org", "https://git.example.org?token=secret", "https://git.example.org#part").forEach {
            assertTrue(ServerAddress.parse(it).isFailure)
        }
    }
}
