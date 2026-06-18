package glowie.glowie_app.comms.internal

import glowie.glowie_app.comms.`interface`.GlowieDevice
import glowie.glowie_app.comms.`interface`.GlowieDeviceConnection
import glowie.glowie_app.comms.`interface`.GlowieDeviceControl
import glowie.glowie_app.comms.internal.transport.GlowieDeviceTransport
import kotlin.math.min

class GlowieDeviceImpl (
    private val glowieDeviceTransport: GlowieDeviceTransport,
) : GlowieDevice,
    GlowieDeviceControl by CmdGlowieDeviceControl(glowieDeviceTransport::sendCommand),
    GlowieDeviceConnection by glowieDeviceTransport
{
    override val info = glowieDeviceTransport.info
}

private class CmdGlowieDeviceControl (
    private val sendCommand: suspend (String) -> Unit,
) : GlowieDeviceControl{
    override suspend fun setLEDsEnabled(enabled: Boolean) {
        sendCommand("led_en ${if (enabled) "1" else "0"}")
    }
    override suspend fun setBrightness(brightness: Float) {
        val brightnessInt: Int = min((brightness * 255).toInt(), 255)
        sendCommand("brightness_set $brightnessInt")
    }
    override suspend fun shutdown() {
        sendCommand("shutdown")
    }
    override suspend fun runScript(script: String) {
        sendCommand("run $script")
    }
}
