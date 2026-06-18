package glowie.glowie_app

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import glowie.glowie_app.control.presentation.ControlView
import glowie.glowie_app.control.presentation.ControlViewPreview

@Composable
fun App(appContainer: AppContainer) {
    MaterialTheme {
        ControlView(appContainer.controlViewModelFactory)
    }
}

@Preview
@Composable
fun AppPreview() {
    MaterialTheme {
        ControlViewPreview()
    }
}