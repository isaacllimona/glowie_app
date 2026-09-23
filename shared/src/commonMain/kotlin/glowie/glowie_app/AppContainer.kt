package glowie.glowie_app

import glowie.glowie_app.presentation.cmderrors.ErrorViewModel
import glowie.glowie_app.core.devices.monitoring.MergedCmdErrFlow
import glowie.glowie_app.comms.BLE.KableGlowieScanner
import glowie.glowie_app.core.devices.control.ControlDispatcher
import glowie.glowie_app.core.devices.control.ControlState
import glowie.glowie_app.presentation.control.ControlViewModel
import glowie.glowie_app.core.devices.management.DeviceManager
import glowie.glowie_app.core.devices.management.DeviceWatcher
import glowie.glowie_app.core.devices.management.GlowieDiscovery
import glowie.glowie_app.presentation.devices.DeviceManagerViewModel
import glowie.glowie_app.core.devices.control.ColorLivePreview
import glowie.glowie_app.presentation.scriptEditor.EditorViewModel
import glowie.glowie_app.core.devices.control.LaunchScript
import glowie.glowie_app.presentation.launcher.LauncherViewModel
import glowie.glowie_app.data.scripts.BuiltinScriptRepository
import glowie.glowie_app.data.scripts.LocalScriptRepository
import glowie.glowie_app.core.scripts.services.ImportBuiltinScripts
import glowie.glowie_app.core.scripts.services.SaveScript
import glowie.glowie_app.core.devices.control.syncClock.SyncClockBroadcaster

class AppContainer() {
    // Initialise communication module
    private val glowieScanner = KableGlowieScanner()

    // Instantiate stateful things that shall live during the entire application
    private val deviceManager = DeviceManager()
    private val controlState = ControlState()
    private val mergedCmdErrFlow = MergedCmdErrFlow(deviceManager)

    private val scriptRepository = LocalScriptRepository()

    // Headless app
    val headlessApp = HeadlessApp(
        GlowieDiscovery(glowieScanner, deviceManager),
        DeviceWatcher(deviceManager),
        ControlDispatcher(controlState, deviceManager),
        SyncClockBroadcaster(deviceManager),
        deviceManager,
        ImportBuiltinScripts(SaveScript(scriptRepository), BuiltinScriptRepository())
    )

    // Define viewmodel factories
    val controlViewModelFactory = {
        ControlViewModel(controlState)
    }
    val launcherViewModelFactory = {
        val launchScript = LaunchScript(deviceManager)
        LauncherViewModel(scriptRepository, launchScript)
    }
    val EditorViewModelFactory = {
        val launchScript = LaunchScript(deviceManager)
        val colorLivePreview = ColorLivePreview(deviceManager)
        val saveUserScript = SaveScript(scriptRepository)
        EditorViewModel(scriptRepository, launchScript, colorLivePreview, saveUserScript)
    }
    val deviceManagerViewModelFactory = {
        DeviceManagerViewModel(deviceManager)
    }
    val errorViewModelFactory = {
        ErrorViewModel(mergedCmdErrFlow)
    }
}
