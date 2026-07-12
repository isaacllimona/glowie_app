package glowie.glowie_app.scriptEditor.domain

import glowie.glowie_app.devices.domain.DeviceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.math.roundToInt

private data class Color (
    val hue: Float,
    val saturation: Float,
    val value: Float,
)

class ColorLivePreview(
    val deviceManager: DeviceManager,
) {
    private val colorFlow = MutableStateFlow(Color(0f, 0f, 0f))

    /**
     * Set the device color, with hue from 0-360, saturation and value from 0-1
     * runLivePreview() must be running for this to take effect
     */
    fun setColor(hue: Float, saturation: Float, value: Float) {
        colorFlow.value = Color(hue, saturation, value)
    }

    suspend fun runLivePreview() {
        deviceManager.applyToAll { runScript("setColorRGB(0,0,0) stop()") }.join()
        try {
            colorFlow
                .collect { color ->
                    val scriptHue = (color.hue / 2).roundToInt() % 360
                    val scriptSat = (color.saturation * 255f).roundToInt()
                    val scriptVal = (color.value * 255f).roundToInt()
                    deviceManager.applyToAll {
                        runScript("setColorHSV($scriptHue, $scriptSat, $scriptVal)")
                    }.join()
                }
        } finally {
            deviceManager.applyToAll { runScript("setColorRGB(0,0,0)") }
        }
    }
}
