package glowie.glowie_app

import glowie.glowie_app.cmderrors.presentation.ErrorViewModel
import glowie.glowie_app.cmderrors.domain.MergedCmdErrFlow
import glowie.glowie_app.comms.internal.BLE.KableGlowieScanner
import glowie.glowie_app.control.domain.ControlDispatcher
import glowie.glowie_app.control.domain.ControlState
import glowie.glowie_app.control.presentation.ControlViewModel
import glowie.glowie_app.devices.domain.DeviceManager
import glowie.glowie_app.devices.domain.DeviceWatcher
import glowie.glowie_app.devices.domain.GlowieDiscovery
import glowie.glowie_app.devices.presentation.DeviceManagerViewModel
import glowie.glowie_app.scriptEditor.domain.GetScriptContent
import glowie.glowie_app.scriptEditor.domain.RunCustomScript
import glowie.glowie_app.scriptEditor.domain.ColorLivePreview
import glowie.glowie_app.scriptEditor.presentation.EditorViewModel
import glowie.glowie_app.launcher.data.BuiltinScriptRepository
import glowie.glowie_app.launcher.domain.LaunchScript
import glowie.glowie_app.launcher.domain.LaunchScriptFromID
import glowie.glowie_app.launcher.presentation.LauncherViewModel
import glowie.glowie_app.syncClock.domain.SyncClockBroadcaster

class AppContainer() {
    // Initialise communication module
    private val glowieScanner = KableGlowieScanner()

    // Instantiate stateful things that shall live during the entire application
    private val deviceManager = DeviceManager()
    private val controlState = ControlState()
    private val mergedCmdErrFlow = MergedCmdErrFlow(deviceManager)

    private val scriptRepository = BuiltinScriptRepository()

    // Headless app
    val headlessApp = HeadlessApp(
        GlowieDiscovery(glowieScanner, deviceManager),
        DeviceWatcher(deviceManager),
        ControlDispatcher(controlState, deviceManager),
        SyncClockBroadcaster(deviceManager),
        deviceManager
    )

    // Define viewmodel factories
    val controlViewModelFactory = {
        ControlViewModel(controlState)
    }
    val launcherViewModelFactory = {
        val launchScriptFromID = LaunchScriptFromID(scriptRepository, LaunchScript(deviceManager))
        LauncherViewModel(scriptRepository, launchScriptFromID)
    }
    val EditorViewModelFactory = { scriptId: String ->
        val runCustomScript = RunCustomScript(LaunchScript(deviceManager))
        val getScriptContent = GetScriptContent(scriptRepository)
        val colorLivePreview = ColorLivePreview(deviceManager)
        EditorViewModel(runCustomScript, getScriptContent, colorLivePreview, scriptId)
    }
    val deviceManagerViewModelFactory = {
        DeviceManagerViewModel(deviceManager)
    }
    val errorViewModelFactory = {
        ErrorViewModel(mergedCmdErrFlow)
    }
}
