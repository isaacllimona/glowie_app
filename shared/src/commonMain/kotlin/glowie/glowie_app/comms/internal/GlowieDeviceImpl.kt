package glowie.glowie_app.comms.internal

import glowie.glowie_app.comms.`interface`.GlowieDevice
import glowie.glowie_app.comms.`interface`.GlowieDeviceConnection
import glowie.glowie_app.comms.`interface`.GlowieDeviceControl
import glowie.glowie_app.comms.internal.transport.GlowieDeviceTransport
import kotlin.math.min

class GlowieDeviceImpl (
    private val glowieDeviceTransport: GlowieDeviceTransport,
) : GlowieDevice,
    GlowieDeviceControl by CmdGlowieDeviceControl(
        glowieDeviceTransport::sendCommand,
        glowieDeviceTransport::getMaxCommandLength),
    GlowieDeviceConnection by glowieDeviceTransport
{
    override val info = glowieDeviceTransport.info
}

private class CmdGlowieDeviceControl (
    private val sendCommand: suspend (String) -> Unit,
    private val getMaxCommandLen: suspend () -> Int
) : GlowieDeviceControl{
    override suspend fun setLEDsEnabled(enabled: Boolean) {
        sendCommand("${Commands.setLEDsEnabled} ${if (enabled) "1" else "0"}")
    }
    override suspend fun setBrightness(brightness: Float) {
        val brightnessInt: Int = min((brightness * 255).toInt(), 255)
        sendCommand("${Commands.setBrigntness} $brightnessInt")
    }
    override suspend fun shutdown() {
        sendCommand("${Commands.shutdown}")
    }
    override suspend fun runScript(script: String) {
        if (script.length == 0) {
            // TODO: log error
            return
        }

        // Maximum script length is max command length minus command + space
        val maxScriptLen = getMaxCommandLen() - Commands.runScript.length - 1

        for (chunk in chunkScript(script, maxScriptLen)) {
            sendCommand("${Commands.runScript} $chunk")
        }
    }
    override suspend fun setTime(time: Int) {
        sendCommand("${Commands.setTime} $time")
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
