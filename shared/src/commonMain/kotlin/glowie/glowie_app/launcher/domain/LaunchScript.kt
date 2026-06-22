package glowie.glowie_app.launcher.domain

import glowie.glowie_app.devices.domain.DeviceManager

class LaunchScript(
    private val scriptRepository: ScriptRepository,
    private val deviceManager: DeviceManager,
) {
    suspend operator fun invoke(scriptId: String) {
        val script = scriptRepository.getScriptContent(scriptId)
        deviceManager.applyToAll {
            runScript("sys.stop()")
            runScript(script)
        }
    }

    fun stop() {
        deviceManager.applyToAll {
            runScript("led.setColorRGB(0,0,0) sys.stop()")
        }
    }
}
