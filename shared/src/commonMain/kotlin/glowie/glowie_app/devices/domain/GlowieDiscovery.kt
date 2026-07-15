package glowie.glowie_app.devices.domain

import glowie.glowie_app.comms.`interface`.GlowieScanner

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