package glowie.glowie_app.comms.BLE

import com.juul.kable.*
import com.juul.kable.logs.Logging.Level.*
import glowie.glowie_app.core.devices.communication.GlowieInfo
import glowie.glowie_app.core.devices.communication.GlowieScanner
import glowie.glowie_app.core.devices.communication.GlowieDevice
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

class KableGlowieScanner : GlowieScanner {
     override fun scan(): Flow<GlowieInfo> = kableScanner.advertisements.transform { advertisement: Advertisement ->
        val device = GlowieInfo(
            name = advertisement.name,
            id = advertisement.identifier.toString()
        )
        emit(device)
    }

    override suspend fun connect(device: GlowieInfo) : GlowieDevice {
        val peripheral = Peripheral(device.id.toIdentifier(), PeripheralBuilder::platformPeripheralBuilder)
        peripheral.connect()
        return KableGlowieDevice(peripheral, device)
    }
}

expect fun PeripheralBuilder.platformPeripheralBuilder(): Unit