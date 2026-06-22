package glowie.glowie_app.devices.presentation

import androidx.lifecycle.ViewModel
import glowie.glowie_app.comms.`interface`.GlowieDevice
import glowie.glowie_app.devices.domain.DeviceManager
import kotlinx.coroutines.flow.StateFlow

class DeviceManagerViewModel(
    private val deviceManager: DeviceManager,
) : ViewModel() {
    val devices: StateFlow<List<GlowieDevice>> = deviceManager.glowiesState

    fun disconnectAll() {
        deviceManager.applyToAll { disconnect() }
    }

    fun shutdownAll() {
        deviceManager.applyToAll { shutdown() }
    }
}
