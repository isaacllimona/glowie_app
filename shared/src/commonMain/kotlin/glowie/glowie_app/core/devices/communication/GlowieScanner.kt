package glowie.glowie_app.core.devices.communication

import kotlinx.coroutines.flow.Flow

interface GlowieScanner {
    fun scan(): Flow<GlowieInfo>
    suspend fun connect(device: GlowieInfo): GlowieDevice
}
