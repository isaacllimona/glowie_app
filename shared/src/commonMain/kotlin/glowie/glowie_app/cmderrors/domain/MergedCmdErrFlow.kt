package glowie.glowie_app.cmderrors.domain

import glowie.glowie_app.comms.`interface`.GlowieDevice
import glowie.glowie_app.devices.domain.DeviceManager
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
