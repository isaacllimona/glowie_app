package glowie.glowie_app.comms.internal.transport.BLE

import com.juul.kable.Peripheral
import com.juul.kable.State
import com.juul.kable.WriteType
import com.juul.kable.characteristicOf
import glowie.glowie_app.comms.`interface`.GlowieInfo
import glowie.glowie_app.comms.internal.transport.GlowieDeviceTransport
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
private object Chars {
    val cmd_in = characteristicOf(
        service = UUIDs.Services.control,
        characteristic = UUIDs.Chars.cmd_in
    )
}

private val COMMAND_WRITE_TYPE = WriteType.WithResponse
private const val MAX_WRITE_LENGTH = 216 // Found experimentally, no idea where it comes from. Firmware is set to

class KableGlowieDevice (
    private val peripheral: Peripheral,
    override val info: GlowieInfo
) : GlowieDeviceTransport {
    override suspend fun sendCommand(command: String) {
        try {
            peripheral.write(
                Chars.cmd_in,
                command.toByteArray(),
                writeType = COMMAND_WRITE_TYPE,
            )
        } catch (e: Exception) {
            println("Could not send command: $command. Exception: $e")
        }
    }

    override suspend fun getMaxCommandLength(): Int = MAX_WRITE_LENGTH //TODO: can I get the acual MTU?

    override suspend fun disconnect() {
        peripheral.disconnect()
    }

    override suspend fun awaitDisconnect() {
        peripheral.state
            .filter { state -> state is State.Disconnected }
            .first()
    }
}
