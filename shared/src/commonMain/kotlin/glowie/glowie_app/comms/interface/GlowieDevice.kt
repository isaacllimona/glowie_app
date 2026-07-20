package glowie.glowie_app.comms.`interface`

import kotlinx.coroutines.flow.Flow

interface GlowieDeviceControl {
    suspend fun setLEDsEnabled(enabled: Boolean)
    suspend fun setBrightness(brightness: Float)
    suspend fun shutdown()
    suspend fun runScript(script: String)
    suspend fun setTime(time: Int)
}

interface GlowieDeviceNotifications {
    fun observeCmdErr() : Flow<String>
}

interface GlowieDeviceConnection {
    suspend fun disconnect()
    suspend fun awaitDisconnect()
}

interface GlowieDevice : GlowieDeviceControl, GlowieDeviceNotifications, GlowieDeviceConnection {
    val info: GlowieInfo
}
