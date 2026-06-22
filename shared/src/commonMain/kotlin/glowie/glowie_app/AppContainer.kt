package glowie.glowie_app

import glowie.glowie_app.comms.internal.GlowieDeviceImpl
import glowie.glowie_app.comms.internal.transport.BLE.KableGlowieScanner
import glowie.glowie_app.control.domain.Control
import glowie.glowie_app.control.presentation.ControlViewModel
import glowie.glowie_app.devices.domain.DeviceManager
import glowie.glowie_app.devices.presentation.DeviceManagerViewModel
import glowie.glowie_app.scriptEditor.domain.RunCustomScript
import glowie.glowie_app.scriptEditor.presentation.EditorViewModel
import glowie.glowie_app.launcher.data.BuiltinScriptRepository
import glowie.glowie_app.launcher.domain.LaunchScript
import glowie.glowie_app.launcher.presentation.LauncherViewModel
import kotlinx.coroutines.CoroutineScope

class AppContainer(appScope: CoroutineScope) {
    // Initialise communication module
    private val glowieScanner = KableGlowieScanner(::GlowieDeviceImpl)

    // Instantiate domains that shall live during the entire application
    val deviceManager = DeviceManager(
        glowieScanner,
        scope = appScope
    )
    val control = Control(
        deviceManager,
        scope = appScope
    )

    private val scriptRepository = BuiltinScriptRepository()

    // Define viewmodel factories
    val controlViewModelFactory = {
        ControlViewModel(control)
    }
    val launcherViewModelFactory = {
        val launchScript = LaunchScript(scriptRepository, deviceManager)
        LauncherViewModel(scriptRepository, launchScript)
    }
    val EditorViewModelFactory = {
        val runCustomScript = RunCustomScript(deviceManager)
        EditorViewModel(runCustomScript)
    }
    val deviceManagerViewModelFactory = {
        DeviceManagerViewModel(deviceManager)
    }
}
