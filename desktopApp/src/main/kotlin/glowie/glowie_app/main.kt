package glowie.glowie_app

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.runBlocking

fun main() = application {
    val appScope = rememberCoroutineScope()
    val appContainer = AppContainer(appScope)
    val headlessApp = HeadlessApp(appContainer)

    headlessApp.start()

    Window(
        onCloseRequest = {
            println("Exiting application...")
            runBlocking { headlessApp.exit() }
            exitApplication()
        } ,
        title = "glowie_app",
    ) {
        App(appContainer)
    }
}
