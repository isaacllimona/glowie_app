package glowie.glowie_app.launcher.domain

import glowie.glowie_app.devices.domain.DeviceManager

class LaunchScript (
    private val deviceManager: DeviceManager
) {
    suspend operator fun invoke(script: String) {
        val compacted_script = compactScript(script)
        deviceManager.applyToAll {
            runScript("stop()")
            runScript(compacted_script)
        }
    }

    suspend fun stop() {
        deviceManager.applyToAll {
            runScript("setColorRGB(0,0,0) stop()")
        }
    }
}