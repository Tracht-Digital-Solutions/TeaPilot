package de.trachtdigital.teapilot

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.trachtdigital.teapilot.core.ApiFailure
import de.trachtdigital.teapilot.core.GiteaTransport
import de.trachtdigital.teapilot.core.ServerAddress
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class SessionState(
    val busy: Boolean = false,
    val username: String? = null,
    val server: String = "",
    val version: String = "",
    val repositories: List<Repository> = emptyList(),
    val hasNext: Boolean = false,
    val error: String? = null,
)

/** Credentials live only in this ViewModel; never in saved state, disk or logs. */
class SessionViewModel : ViewModel() {
    var state by mutableStateOf(SessionState())
        private set
    private var api: GiteaApi? = null
    private var token: String? = null
    private var page = 1
    private var request: Job? = null

    fun login(address: String, credential: String) {
        if (state.busy) return
        val server = ServerAddress.parse(address).getOrElse {
            state = SessionState(error = "Bitte eine gültige HTTPS-Serveradresse eingeben.")
            return
        }
        if (credential.isEmpty() || credential.length > 4096 || credential.any { it.code !in 33..126 }) {
            state = SessionState(error = "Bitte einen gültigen persönlichen Token eingeben.")
            return
        }
        request = viewModelScope.launch {
            state = SessionState(busy = true)
            try {
                val client = GiteaApi(GiteaTransport(server))
                val result = withContext(Dispatchers.IO) {
                    val version = client.version()
                    val username = client.username(credential)
                    Triple(version, username, client.repositories(credential, 1))
                }
                api = client
                token = credential
                page = 1
                state = SessionState(username = result.second, server = server.value, version = result.first,
                    repositories = result.third.repositories, hasNext = result.third.hasNext)
            } catch (error: Exception) {
                if (error is CancellationException) throw error
                state = SessionState(error = message(error))
            }
        }
    }

    fun loadMore(refresh: Boolean = false) {
        val client = api ?: return
        val credential = token ?: return
        if (state.busy || (!refresh && !state.hasNext)) return
        val next = if (refresh) 1 else page + 1
        request = viewModelScope.launch {
            state = state.copy(busy = true, error = null)
            try {
                val result = withContext(Dispatchers.IO) { client.repositories(credential, next) }
                page = next
                state = state.copy(busy = false,
                    repositories = (if (refresh) result.repositories else state.repositories + result.repositories).distinctBy { it.id },
                    hasNext = result.hasNext)
            } catch (error: Exception) {
                if (error is CancellationException) throw error
                if (error is ApiFailure && error.status == 401) {
                    logout()
                    state = SessionState(error = "Die Anmeldung ist abgelaufen. Bitte erneut anmelden.")
                } else state = state.copy(busy = false, error = message(error))
            }
        }
    }

    fun logout() {
        request?.cancel()
        api = null
        token = null
        page = 1
        state = SessionState()
    }

    private fun message(error: Exception): String = when (error) {
        is ApiFailure -> when (error.status) {
            401 -> "Token ungültig oder abgelaufen."
            403 -> "Zugriff verweigert. Token-Berechtigungen für Benutzer und Repositories prüfen."
            404 -> "Gitea-API nicht gefunden. Bitte die Serveradresse prüfen."
            429 -> "Zu viele Anfragen. Bitte später erneut versuchen."
            in 300..399 -> "Der Server leitet weiter. Bitte direkt seine endgültige HTTPS-Adresse eingeben."
            else -> "Der Gitea-Server antwortet mit einem Fehler (HTTP ${error.status})."
        }
        is IOException -> "Verbindung fehlgeschlagen. Netzwerk, Server und HTTPS-Zertifikat prüfen."
        else -> "Die Serverantwort konnte nicht gelesen werden. Ist dies ein Gitea-Server?"
    }
}
