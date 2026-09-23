package glowie.glowie_app.presentation.devices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import glowie.glowie_app.core.devices.communication.GlowieDevice
import glowie.glowie_app.core.devices.management.DeviceManager
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DeviceManagerViewModel(
    private val deviceManager: DeviceManager,
) : ViewModel() {
    val devices: StateFlow<List<GlowieDevice>> = deviceManager.glowiesState

    fun disconnectAll() = viewModelScope.launch {
        deviceManager.applyToAll { disconnect() }
    }

    fun shutdownAll() = viewModelScope.launch {
        deviceManager.applyToAll { shutdown() }
    }
}
