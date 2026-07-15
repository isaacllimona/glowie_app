package glowie.glowie_app.devices.domain

import glowie.glowie_app.comms.`interface`.GlowieDevice
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class DeviceWatcher (
    private val deviceManager: DeviceManager,
) {
    suspend fun run() : Nothing = coroutineScope {
        deviceManager.newConnectionsFlow.collect { device ->
            println("Watching device ${device.info}")
            this@coroutineScope.launch { watchConnection(device) }
        }
    }

    private suspend fun watchConnection(device: GlowieDevice) {
        device.awaitDisconnect()
        deviceManager.removeDevice(device)
    }
}