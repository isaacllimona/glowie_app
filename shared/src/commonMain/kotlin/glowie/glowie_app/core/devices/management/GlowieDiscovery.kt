package glowie.glowie_app.core.devices.management

import glowie.glowie_app.core.devices.communication.GlowieScanner

class GlowieDiscovery (
    private val glowieScanner: GlowieScanner,
    private val deviceManager: DeviceManager,
) {
    suspend fun run() {
        glowieScanner.scan().collect { connectable ->
            if (deviceManager.glowies.any { it.info.id == connectable.id }) {
                println("Found a glowie that is already connected")
                return@collect
            }
            val newGlowie = glowieScanner.connect(connectable)
            deviceManager.addDevice(newGlowie)
        }
    }
}