package glowie.glowie_app.devices.domain

import glowie.glowie_app.comms.`interface`.GlowieScanner
import glowie.glowie_app.comms.`interface`.GlowieDevice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
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

    fun applyToAll(func: suspend GlowieDevice.() -> Unit) : Job = communicationScope.launch {
        glowies.forEach {
            launch { it.func() }
        }
    }

    // Headless

    suspend fun runGlowieDiscovery() {
        glowieScanner.scan().collect { connectable ->
            if (glowies.any {it.info.id == connectable.id}) {
                println("Found a glowie that is already connected")
                return@collect
            }
            val newGlowie = glowieScanner.connect(connectable)
            addGlowie(newGlowie)
        }
    }

    private fun addGlowie(newGlowie: GlowieDevice) {
        println("New glowie connected!")

        _glowies.add(newGlowie)
        communicationScope.launch {
            newGlowie.awaitDisconnect()
            onDisconnect(newGlowie)
        }
    }

    private fun onDisconnect(device: GlowieDevice) {
        println("Glowie disconnected!")
        _glowies.remove(device)
    }
}
