package glowie.glowie_app

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import glowie.glowie_app.presentation.App
import kotlinx.coroutines.launch

fun main() = application {
    val appScope = rememberCoroutineScope()
    val appContainer = AppContainer()

    appScope.launch { appContainer.headlessApp() }

    Window(
        onCloseRequest = {
            println("Exiting application...")
            exitApplication()
        } ,
        title = "glowie_app",
    ) {
        App(appContainer)
    }
}
