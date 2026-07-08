package glowie.glowie_app.comms.`interface`

interface GlowieDeviceControl {
    suspend fun setLEDsEnabled(enabled: Boolean)
    suspend fun setBrightness(brightness: Float)
    suspend fun shutdown()
    suspend fun runScript(script: String)
    suspend fun setTime(time: Int)
}

interface GlowieDeviceConnection {
    suspend fun disconnect()
    suspend fun awaitDisconnect()
}

interface GlowieDevice : GlowieDeviceControl, GlowieDeviceConnection {
    val info: GlowieInfo
}
