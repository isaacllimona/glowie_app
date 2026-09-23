package glowie.glowie_app.comms.BLE

import com.juul.kable.Peripheral
import com.juul.kable.State
import com.juul.kable.WriteType
import com.juul.kable.characteristicOf
import glowie.glowie_app.core.devices.communication.GlowieDevice
import glowie.glowie_app.core.devices.communication.GlowieDeviceConnection
import glowie.glowie_app.core.devices.communication.GlowieInfo
import glowie.glowie_app.core.devices.communication.GlowieDeviceControl
import glowie.glowie_app.core.devices.communication.GlowieDeviceNotifications
import glowie.glowie_app.comms.GlowieCommands
import glowie.glowie_app.comms.GlowieDeviceCommandSender
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
private object Chars {
    val cmd_in = characteristicOf(
        service = UUIDs.Services.control,
        characteristic = UUIDs.Chars.cmd_in
    )
    val cmd_err = characteristicOf(
        service = UUIDs.Services.control,
        characteristic = UUIDs.Chars.cmd_err
    )
}

private val COMMAND_WRITE_TYPE = WriteType.WithResponse
private const val MAX_WRITE_LENGTH = 216 // Found experimentally, no idea where it comes from. Firmware is set to

class KableGlowieDeviceCommandSender (private val peripheral: Peripheral) : GlowieDeviceCommandSender {
    override suspend fun sendCommand(cmd: String) {
        try {
            peripheral.write(
                Chars.cmd_in,
                cmd.toByteArray(),
                writeType = COMMAND_WRITE_TYPE,
            )
        } catch (e: Exception) {
            println("Could not send command: $cmd. Exception: $e")
        }
    }

    override suspend fun getMaxCommandLen(): Int = MAX_WRITE_LENGTH
}

class KableGlowieDeviceNotifications (private val peripheral: Peripheral) : GlowieDeviceNotifications {
    override fun observeCmdErr(): Flow<String> =
        peripheral.observe(Chars.cmd_err)
            .catch { exception -> println("Error in cmdErr observer: $exception") }
            .map { it.toString(Charsets.UTF_8) }
}

class KableGlowieDeviceConnection (private val peripheral: Peripheral) : GlowieDeviceConnection {
    override suspend fun disconnect() {
        peripheral.disconnect()
    }

    override suspend fun awaitDisconnect() {
        peripheral.state
            .filter { state -> state is State.Disconnected }
            .first()
    }
}

class KableGlowieDevice(
    peripheral: Peripheral,
    override val info: GlowieInfo
    ) : GlowieDevice,
        GlowieDeviceControl by GlowieCommands(KableGlowieDeviceCommandSender(peripheral)),
        GlowieDeviceNotifications by KableGlowieDeviceNotifications(peripheral),
        GlowieDeviceConnection by KableGlowieDeviceConnection(peripheral)

