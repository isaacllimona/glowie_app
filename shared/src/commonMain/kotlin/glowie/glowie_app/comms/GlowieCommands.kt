package glowie.glowie_app.comms

import glowie.glowie_app.core.devices.communication.GlowieDeviceControl
import kotlin.math.min

interface GlowieDeviceCommandSender {
    suspend fun sendCommand(cmd: String): Unit
    suspend fun getMaxCommandLen(): Int
}

class GlowieCommands (
    private val commandSender: GlowieDeviceCommandSender
) : GlowieDeviceControl{
    override suspend fun setLEDsEnabled(enabled: Boolean) {
        commandSender.sendCommand("${Commands.setLEDsEnabled} ${if (enabled) "1" else "0"}")
    }
    override suspend fun setBrightness(brightness: Float) {
        val brightnessInt: Int = min((brightness * 255).toInt(), 255)
        commandSender.sendCommand("${Commands.setBrigntness} $brightnessInt")
    }
    override suspend fun shutdown() {
        commandSender.sendCommand("${Commands.shutdown}")
    }
    override suspend fun runScript(script: String) {
        if (script.length == 0) {
            // TODO: log error
            return
        }

        // Maximum script length is max command length minus command + space
        val maxScriptLen = commandSender.getMaxCommandLen() - Commands.runScript.length - 1

        for (chunk in chunkScript(script, maxScriptLen)) {
            commandSender.sendCommand("${Commands.runScript} $chunk")
        }
    }
    override suspend fun setTime(time: Int) {
        commandSender.sendCommand("${Commands.setTime} $time")
    }
}

private fun chunkScript(script: String, maxScriptLen: Int): Sequence<String> = sequence {
    val scriptLen = script.length
    var idx = 0
    while (idx + maxScriptLen < scriptLen) {
        val end = idx + maxScriptLen - 1  // -1 for the continuation character (\)
        val chunk = script.substring(idx, end)
        yield(chunk + "\\")
        idx = end
    }
    // Last chunk -- without continuation character
    val chunk = script.substring(idx)
    yield(chunk)
}

private object Commands {
    const val setLEDsEnabled = "led_en"
    const val setBrigntness = "brightness_set"
    const val shutdown = "shutdown"
    const val runScript = "run"
    const val setTime = "time_set"
}
