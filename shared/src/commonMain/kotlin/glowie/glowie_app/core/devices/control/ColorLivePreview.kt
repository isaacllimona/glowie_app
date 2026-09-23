package glowie.glowie_app.core.devices.control

import glowie.glowie_app.core.devices.management.DeviceManager
import kotlinx.coroutines.flow.MutableStateFlow

private data class Color (
    val r: Int,
    val g: Int,
    val b: Int,
)

class ColorLivePreview(
    val deviceManager: DeviceManager,
) {
    private val colorFlow = MutableStateFlow(Color(0, 0, 0))

    /**
     * Set the device color, with r, g, b from 0-255
     * runLivePreview() must be running for this to take effect
     */
    fun setColor(r: Int, g: Int, b: Int) {
        colorFlow.value = Color(r, g, b)
    }

    suspend fun runLivePreview() {
        deviceManager.applyToAll { runScript(scriptBits.stopAndClear) }
        try {
            colorFlow
                .collect { color ->
                    deviceManager.applyToAll {
                        runScript(scriptBits.setColor(color.r, color.g, color.b))
                    }
                }
        } finally {
            deviceManager.applyToAll { runScript(scriptBits.stopAndClear) }
        }
    }
}
