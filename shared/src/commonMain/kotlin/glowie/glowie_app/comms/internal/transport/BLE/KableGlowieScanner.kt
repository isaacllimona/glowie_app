package glowie.glowie_app.comms.internal.transport.BLE

import com.juul.kable.*
import com.juul.kable.logs.Logging.Level.*
import glowie.glowie_app.comms.`interface`.GlowieInfo
import glowie.glowie_app.comms.`interface`.GlowieScanner
import glowie.glowie_app.comms.`interface`.GlowieDevice
import glowie.glowie_app.comms.internal.transport.GlowieDeviceTransport
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
val kableScanner = Scanner {
    filters {
        match { services = listOf(UUIDs.Services.control) }
    }
    logging { level = Data }
}

class KableGlowieScanner (
    private val deviceBuilder: (GlowieDeviceTransport) -> GlowieDevice
) : GlowieScanner {
     override fun scan(): Flow<GlowieInfo> = kableScanner.advertisements.transform { advertisement: Advertisement ->
        val device = GlowieInfo(
            name = advertisement.name,
            id = advertisement.identifier.toString()
        )
        emit(device)
    }

    override suspend fun connect(device: GlowieInfo) : GlowieDevice {
        val peripheral = Peripheral(device.id.toIdentifier())
        peripheral.connect()
        val kableGlowieDevice = KableGlowieDevice(peripheral, device)
        return deviceBuilder(kableGlowieDevice)
    }
}
