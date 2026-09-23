package glowie.glowie_app.core.devices.control

import glowie.glowie_app.core.devices.management.DeviceManager
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * ControlDispatcher is responsible for dispatching enabled and brightness state
 * changes to connected devices as well as sending enabled and brightness state
 * to newly connected devices.
 */
class ControlDispatcher(
    val controlState: ControlState,
    val deviceManager: DeviceManager,
) {
    suspend fun run(): Nothing = coroutineScope {
        // Dispatch brightness changes
        launch {
            controlState.brightnessFlow.collect { brightness ->
                deviceManager.applyToAll { setBrightness(brightness) }
            }
        }
        // Dispatch enabled changes
        launch {
            controlState.enabledFlow.collect { enabled ->
                deviceManager.applyToAll { setLEDsEnabled(enabled) }
            }
        }
        // Dispatch control state to new devices
        launch {
            deviceManager.newConnectionsFlow.collect { newDevice ->
                newDevice.setBrightness(controlState.brightness)
                newDevice.setLEDsEnabled(controlState.enabled)
            }
        }
        // This function will never return normally as the launched coroutines won't either.
        // The compiler does not understand this so we call awaitCancellation() to force it.
        awaitCancellation()
    }
}
