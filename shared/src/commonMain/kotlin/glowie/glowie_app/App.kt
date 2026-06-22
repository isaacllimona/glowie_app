package glowie.glowie_app

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import glowie.glowie_app.control.presentation.ControlBar
import glowie.glowie_app.control.presentation.ControlBarPreview
import glowie.glowie_app.launcher.domain.Script
import glowie.glowie_app.launcher.presentation.LauncherScreen

@Composable
fun App(appContainer: AppContainer) {
    MaterialTheme {
        Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.systemBars)) {
            ControlBar(viewModelFactory = appContainer.controlViewModelFactory)
            LauncherScreen(viewModelFactory = appContainer.launcherViewModelFactory)
        }
    }
}

@Preview
@Composable
fun AppPreview() {
    MaterialTheme {
        Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.systemBars)) {
            ControlBarPreview()
            LauncherScreen(
                scripts = listOf(
                    Script("", "blue", ""),
                    Script("", "red", ""),
                    Script("", "green", "")
                ),
            )
        }
    }
}
