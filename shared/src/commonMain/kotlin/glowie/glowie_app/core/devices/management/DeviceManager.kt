package glowie.glowie_app.core.devices.management

import glowie.glowie_app.core.devices.communication.GlowieDevice
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArrayList

class DeviceManager {
    private val _glowies = CopyOnWriteArrayList<GlowieDevice>()
    val glowies:  List<GlowieDevice>
        get() = _glowies.toList()

    private val _glowiesState = MutableStateFlow<List<GlowieDevice>>(emptyList())
    val glowiesState = _glowiesState.asStateFlow()

    private val _newConnectionsFlow = MutableSharedFlow<GlowieDevice>()
    val newConnectionsFlow = _newConnectionsFlow.asSharedFlow()

    suspend fun applyToAll(func: suspend GlowieDevice.() -> Unit) = coroutineScope {
        glowies.forEach {
            launch { it.func() }
        }
    }

    internal suspend fun addDevice(device: GlowieDevice) {
        println("New glowie connected!")
        _glowies.add(device)
        _glowiesState.value = glowies
        _newConnectionsFlow.emit(device)
    }

    internal fun removeDevice(device: GlowieDevice) {
        println("Glowie disconnected!")
        _glowies.remove(device)
        _glowiesState.value = glowies
    }
}
