package glowie.glowie_app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import glowie.glowie_app.cmderrors.presentation.ErrorDialog
import glowie.glowie_app.control.presentation.ControlBar
import glowie.glowie_app.devices.presentation.DeviceManagerScreen
import glowie.glowie_app.launcher.presentation.LauncherScreen
import glowie.glowie_app.scriptEditor.presentation.EditorScreen
import glowie.glowie_app.scripts.domain.ScriptId
import kotlinx.serialization.Serializable

@Serializable
object Home
@Serializable
data class ScriptEditor(val scriptId: String? = null)
@Serializable
object Devices

@Composable
fun App(appContainer: AppContainer) {
    MaterialTheme {
        val navController = rememberNavController()
        Box(Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = Home,
                modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.systemBars),
            ) {
                composable<Home> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        ControlBar(viewModelFactory = appContainer.controlViewModelFactory)

                        LauncherScreen(
                            viewModelFactory = appContainer.launcherViewModelFactory,
                            onNewScriptClick = { navController.navigate(ScriptEditor()) },
                            onEditScript = { scriptId -> navController.navigate(ScriptEditor(scriptId = scriptId.value)) },
                            modifier = Modifier.weight(1f),
                        )

                        Surface(modifier = Modifier.fillMaxWidth()) {
                            Button(
                                onClick = { navController.navigate(Devices) },
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                            ) {
                                Text("Devices")
                            }
                        }
                    }
                }
                composable<ScriptEditor> { backStackEntry ->
                    val route = backStackEntry.toRoute<ScriptEditor>()
                    Column(modifier = Modifier.fillMaxSize()) {
                        EditorScreen(
                            viewModelFactory = {
                                appContainer.EditorViewModelFactory().apply {
                                    if (route.scriptId != null) openScript(ScriptId(route.scriptId))
                                    else newScript()
                                }
                            },
                            onBackClick = { navController.popBackStack() },
                        )
                    }
                }
                composable<Devices> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        DeviceManagerScreen(
                            viewModelFactory = appContainer.deviceManagerViewModelFactory,
                            onBackClick = { navController.popBackStack() },
                        )
                    }
                }
            }
            ErrorDialog(viewModelFactory = appContainer.errorViewModelFactory)
        }
    }
}
