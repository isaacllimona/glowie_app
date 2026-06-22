package glowie.glowie_app.scriptEditor.domain

import glowie.glowie_app.devices.domain.DeviceManager

class RunCustomScript(
    private val deviceManager: DeviceManager,
) {
    suspend operator fun invoke(script: String) {
        deviceManager.applyToAll {
            runScript("sys.stop()")
            runScript(script)
        }
    }
}
