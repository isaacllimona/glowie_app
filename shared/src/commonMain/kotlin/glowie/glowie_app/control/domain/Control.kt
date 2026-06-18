package glowie.glowie_app.control.domain

import glowie.glowie_app.devices.domain.DeviceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class Control (
    val deviceManager: DeviceManager,
    val scope: CoroutineScope,
) {
    fun setEnabled(enabled: Boolean) {
        deviceManager.applyToAll {
            setLEDsEnabled(enabled)
            if (enabled) runScript("led.setColorRGB(50,100,100)")
            else runScript("led.setColorRGB(0,0,0)")
        }
    }

    // Brightness is set through a conflated flow so that intermediate values are skipped
    // if multiple brightness changes occur before the last one has been sent to the devices
    private val brightnessFlow = MutableStateFlow<Float>(0f)

    fun setBrightness(brightness: Float) {
        brightnessFlow.value = brightness
    }

    private suspend fun runBrightnessDispatcher() {
        brightnessFlow
            .collect { brightness ->
                deviceManager.applyToAll { setBrightness(brightness) }
                    .join()
            }
    }

    init {
        scope.launch { runBrightnessDispatcher() }
    }
}
