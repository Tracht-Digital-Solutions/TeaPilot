package de.trachtdigital.teapilot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val colors = if (isSystemInDarkTheme()) darkColorScheme(primary = Color(0xFF91D8C1), background = Color(0xFF101D2D))
                else lightColorScheme(primary = Color(0xFF163852), secondary = Color(0xFF216B5B))
            MaterialTheme(colorScheme = colors) {
                Surface(Modifier.fillMaxSize()) { TeaPilotScreen() }
            }
        }
    }
}

@Composable
private fun TeaPilotScreen(model: SessionViewModel = viewModel()) {
    val state = model.state
    Box(Modifier.fillMaxSize().safeDrawingPadding().imePadding(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(Modifier.widthIn(max = 840.dp).fillMaxSize(), contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                Text(stringResource(R.string.app_name), style = MaterialTheme.typography.displaySmall)
                Text(stringResource(R.string.tagline), style = MaterialTheme.typography.titleMedium)
            }
            if (state.username == null) {
                item { LoginForm(state.busy, model::login) }
            } else {
                item {
                    Text(stringResource(R.string.signed_in, state.username), style = MaterialTheme.typography.titleLarge)
                    Text(state.server, style = MaterialTheme.typography.bodySmall)
                    Text("Gitea ${state.version}", style = MaterialTheme.typography.bodySmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        TextButton(onClick = { model.loadMore(refresh = true) }, enabled = !state.busy) { Text(stringResource(R.string.refresh)) }
                        TextButton(onClick = model::logout) { Text(stringResource(R.string.logout)) }
                    }
                    Text(stringResource(R.string.repositories), style = MaterialTheme.typography.headlineSmall)
                }
                items(state.repositories, key = { it.id }) { repository ->
                    OutlinedCard(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(repository.fullName, style = MaterialTheme.typography.titleMedium)
                            Text(stringResource(if (repository.isPrivate) R.string.private_repository else R.string.public_repository), style = MaterialTheme.typography.labelMedium)
                            if (repository.description.isNotBlank()) Text(repository.description)
                        }
                    }
                }
                if (state.repositories.isEmpty() && !state.busy && state.error == null) item { Text(stringResource(R.string.empty_repositories)) }
                if (state.hasNext) item {
                    OutlinedButton(onClick = { model.loadMore() }, enabled = !state.busy) { Text(stringResource(R.string.load_more)) }
                }
            }
            if (state.busy) item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
            state.error?.let { error -> item {
                Text(error, color = MaterialTheme.colorScheme.error, modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite })
            } }
            item { Text(stringResource(R.string.session_notice), style = MaterialTheme.typography.bodySmall) }
        }
    }
}

@Composable
private fun LoginForm(busy: Boolean, login: (String, String) -> Unit) {
    // Raw input can itself contain credentials; never persist it before validation.
    var address by remember { mutableStateOf("") }
    // A token must never enter Bundle-backed rememberSaveable state.
    var token by remember { mutableStateOf("") }
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(stringResource(R.string.login_intro))
        OutlinedTextField(address, { address = it }, label = { Text(stringResource(R.string.server)) },
            singleLine = true, enabled = !busy, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(token, { token = it }, label = { Text(stringResource(R.string.token)) },
            singleLine = true, enabled = !busy, visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, autoCorrectEnabled = false), modifier = Modifier.fillMaxWidth())
        Text(stringResource(R.string.scopes), style = MaterialTheme.typography.bodySmall)
        Button(onClick = { login(address, token) }, enabled = !busy && address.isNotBlank() && token.isNotBlank()) {
            Text(stringResource(R.string.login))
        }
    }
}
