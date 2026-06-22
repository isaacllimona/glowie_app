package glowie.glowie_app.devices.domain

import glowie.glowie_app.comms.`interface`.GlowieScanner
import glowie.glowie_app.comms.`interface`.GlowieDevice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.plus
import java.util.concurrent.CopyOnWriteArrayList

class DeviceManager (
    private val glowieScanner: GlowieScanner,
    scope: CoroutineScope
) {
    private val communicationScope = scope + Dispatchers.IO

    private val _glowies = CopyOnWriteArrayList<GlowieDevice>()
    val glowies:  List<GlowieDevice>
        get() = _glowies

    private val _glowiesState = MutableStateFlow<List<GlowieDevice>>(emptyList())
    val glowiesState: StateFlow<List<GlowieDevice>> = _glowiesState.asStateFlow()

    fun applyToAll(func: suspend GlowieDevice.() -> Unit) : Job = communicationScope.launch {
        glowies.forEach {
            launch { it.func() }
        }
    }

    // Headless

    var discoveryJob: Job? = null

    fun startGlowieDiscovery() {
        if (discoveryJob != null) {
            println("glowie discovery is already running")
            return
        }

        discoveryJob = communicationScope.launch {
            glowieScanner.scan().collect { connectable ->
                if (glowies.any { it.info.id == connectable.id }) {
                    println("Found a glowie that is already connected")
                    return@collect
                }
                val newGlowie = glowieScanner.connect(connectable)
                addGlowie(newGlowie)
            }
        }
    }

    fun stopGlowieDiscovery() {
        discoveryJob?.cancel()
    }

    suspend fun stopGlowieDiscoveryAndJoin() {
        discoveryJob?.cancelAndJoin()
    }

    private fun addGlowie(newGlowie: GlowieDevice) {
        println("New glowie connected!")

        _glowies.add(newGlowie)
        _glowiesState.value = _glowies.toList()
        communicationScope.launch {
            newGlowie.awaitDisconnect()
            onDisconnect(newGlowie)
        }
    }

    private fun onDisconnect(device: GlowieDevice) {
        println("Glowie disconnected!")
        _glowies.remove(device)
        _glowiesState.value = _glowies.toList()
    }
}
