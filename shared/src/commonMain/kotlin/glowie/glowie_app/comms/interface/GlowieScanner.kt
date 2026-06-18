package glowie.glowie_app.comms.`interface`

import kotlinx.coroutines.flow.Flow

interface GlowieScanner {
    fun scan(): Flow<GlowieInfo>
    suspend fun connect(device: GlowieInfo): GlowieDevice
}
