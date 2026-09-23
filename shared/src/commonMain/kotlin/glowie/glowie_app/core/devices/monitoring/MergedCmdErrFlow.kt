package glowie.glowie_app.core.devices.monitoring

import glowie.glowie_app.core.devices.communication.GlowieDevice
import glowie.glowie_app.core.devices.management.DeviceManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class MergedCmdErrFlow(
    private val deviceManager: DeviceManager,
) {
    private val _errors = Channel<Pair<String, String>>(Channel.BUFFERED)
    val errors = _errors.receiveAsFlow()

    suspend fun run(): Nothing = coroutineScope {
        deviceManager.newConnectionsFlow.collect { device ->
            this@coroutineScope.launch {
                observeDeviceCmdErr(device)
            }
        }
    }

    private suspend fun observeDeviceCmdErr(device: GlowieDevice) = coroutineScope {
        device.observeCmdErr().collect { message ->
            _errors.send((device.info.name ?: "Unnamed device") to message)
        }
    }
}
