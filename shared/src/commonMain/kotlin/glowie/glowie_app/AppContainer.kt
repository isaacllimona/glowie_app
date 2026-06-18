package glowie.glowie_app

import glowie.glowie_app.comms.internal.GlowieDeviceImpl
import glowie.glowie_app.comms.internal.transport.BLE.KableGlowieScanner
import glowie.glowie_app.control.domain.Control
import glowie.glowie_app.control.presentation.ControlViewModel
import glowie.glowie_app.devices.domain.DeviceManager
import kotlinx.coroutines.CoroutineScope

class AppContainer(appScope: CoroutineScope) {
    // Initialise communication module
    private val glowieScanner = KableGlowieScanner(::GlowieDeviceImpl)

    // Instantiate domains
    val deviceManager = DeviceManager(
        glowieScanner,
        scope = appScope
    )
    val control = Control(
        deviceManager,
        scope = appScope
    )

    // Define viewmodel factories
    val controlViewModelFactory = { ControlViewModel(control) }
}
