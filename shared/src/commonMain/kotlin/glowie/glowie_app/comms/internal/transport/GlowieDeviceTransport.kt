package glowie.glowie_app.comms.internal.transport

import glowie.glowie_app.comms.`interface`.GlowieDeviceConnection
import glowie.glowie_app.comms.`interface`.GlowieInfo

interface GlowieDeviceTransport : GlowieDeviceConnection {
    val info: GlowieInfo
    suspend fun sendCommand(command: String)
}