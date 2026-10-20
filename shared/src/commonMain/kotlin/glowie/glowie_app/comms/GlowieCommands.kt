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

        val maxCommandLen = commandSender.getMaxCommandLen()
        if (Commands.runScript.length + 1 + script.length <= maxCommandLen) {
            commandSender.sendCommand("${Commands.runScript} $script")
        }
        else {
            for (cmd in generateMultipartScriptCommands(script, maxCommandLen)) {
                commandSender.sendCommand(cmd)
            }
        }
    }
    override suspend fun setTime(time: Int) {
        commandSender.sendCommand("${Commands.setTime} $time")
    }
}

private fun generateMultipartScriptCommands(script: String, maxCommandLength: Int) = sequence {
    var seq = 0
    var scriptCursorIdx = 0

    fun makePacket(header: String) : String {
        val packetDataLength = min(maxCommandLength - header.length, script.length - scriptCursorIdx)
        val packet = header + script.substring(scriptCursorIdx, scriptCursorIdx + packetDataLength)
        scriptCursorIdx += packetDataLength

        return packet
    }

    val header = "${Commands.runMultipartScript} 0 ${script.length} "
    yield(makePacket(header))
    seq += 1

    while (scriptCursorIdx < script.length) {
        val header = "${Commands.runMultipartScript} $seq "
        yield(makePacket(header))
        seq += 1
    }

    yield("${Commands.runMultipartScript} \$$seq")
}

private object Commands {
    const val setLEDsEnabled = "led_en"
    const val setBrigntness = "brightness_set"
    const val shutdown = "shutdown"
    const val runScript = "run"
    const val runMultipartScript = "mrun"
    const val setTime = "time_set"
}
