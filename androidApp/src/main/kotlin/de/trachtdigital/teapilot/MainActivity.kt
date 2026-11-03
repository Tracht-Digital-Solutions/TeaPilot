package de.trachtdigital.teapilot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.trachtdigital.teapilot.core.ServerAddress

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(Modifier.fillMaxSize()) {
                    var address by rememberSaveable { mutableStateOf("") }
                    var status by rememberSaveable { mutableStateOf("") }
                    Column(Modifier.safeDrawingPadding().verticalScroll(rememberScrollState()).padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        Text("TeaPilot", style = MaterialTheme.typography.displaySmall)
                        Text("Dein Gitea. Dein Überblick.", style = MaterialTheme.typography.headlineSmall)
                        OutlinedTextField(address, { address = it; status = "" }, label = { Text("Gitea-Server (HTTPS)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                        Button(onClick = {
                            status = ServerAddress.parse(address).fold(
                                { "Adresse gültig: ${it.value}. Anmeldung folgt im nächsten Entwicklungsschritt." },
                                { "Bitte eine gültige HTTPS-Adresse ohne Zugangsdaten oder Suchparameter eingeben." }
                            )
                        }, enabled = address.isNotBlank()) { Text("Adresse prüfen") }
                        if (status.isNotBlank()) Text(status)
                        Text("Ohne Telemetrie · Entwicklungsstand 0.1")
                    }
                }
            }
        }
    }
}
