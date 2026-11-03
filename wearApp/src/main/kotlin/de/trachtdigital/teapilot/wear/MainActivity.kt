package de.trachtdigital.teapilot.wear

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Scaffold(timeText = { TimeText() }) {
                    ScalingLazyColumn {
                        item { Text("TeaPilot") }
                        item { Text("Dein Gitea am Handgelenk") }
                        item { Text("Entwicklungsstart: Anmeldung und Benachrichtigungen folgen.") }
                    }
                }
            }
        }
    }
}
