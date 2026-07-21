package glowie.glowie_app.launcher.domain

import glowie.glowie_app.common.scriptBits
import glowie.glowie_app.devices.domain.DeviceManager

class LaunchScript (
    private val deviceManager: DeviceManager
) {
    suspend operator fun invoke(script: String) {
        val compacted_script = compactScript(script)
        deviceManager.applyToAll {
            runScript(scriptBits.stopAndClear)
            runScript(compacted_script)
        }
    }

    suspend fun stop() {
        deviceManager.applyToAll {
            runScript(scriptBits.stopAndClear)
        }
    }
}