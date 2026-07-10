package glowie.glowie_app.comms.internal

import glowie.glowie_app.comms.`interface`.GlowieInfo
import glowie.glowie_app.comms.internal.transport.GlowieDeviceTransport
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GlowieDeviceImplTest {

    private fun createTransport(
        maxCommandLen: Int,
        sentCommands: MutableList<String> = mutableListOf()
    ): GlowieDeviceTransport = object : GlowieDeviceTransport {
        override val info: GlowieInfo = GlowieInfo(name = "test", id = "test-id")

        override suspend fun sendCommand(command: String) {
            sentCommands.add(command)
        }

        override suspend fun getMaxCommandLength(): Int = maxCommandLen

        override suspend fun disconnect() {}

        override suspend fun awaitDisconnect() {}
    }

    @Test
    fun runScript_shortScriptSingleChunk() = runTest {
        val sentCommands = mutableListOf<String>()
        val transport = createTransport(maxCommandLen = 10, sentCommands = sentCommands)
        val device = GlowieDeviceImpl(transport)

        device.runScript("abc")

        assertEquals(1, sentCommands.size)
        assertEquals("run abc", sentCommands[0])
        assertEquals(7, sentCommands[0].length)
    }

    @Test
    fun runScript_scriptExactlyMaxScriptLen() = runTest {
        val sentCommands = mutableListOf<String>()
        val transport = createTransport(maxCommandLen = 10, sentCommands = sentCommands)
        val device = GlowieDeviceImpl(transport)

        device.runScript("abcdef")

        assertEquals(1, sentCommands.size)
        assertEquals("run abcdef", sentCommands[0])
    }

    @Test
    fun runScript_scriptOneOverMaxSplitsIntoTwoChunks() = runTest {
        val sentCommands = mutableListOf<String>()
        val transport = createTransport(maxCommandLen = 10, sentCommands = sentCommands)
        val device = GlowieDeviceImpl(transport)

        device.runScript("abcdefg")

        assertEquals(2, sentCommands.size)
        assertEquals("run abcde\\", sentCommands[0])
        assertEquals(10, sentCommands[0].length)
        assertEquals("run fg", sentCommands[1])
    }

    @Test
    fun runScript_longScriptMultipleChunks() = runTest {
        val sentCommands = mutableListOf<String>()
        val transport = createTransport(maxCommandLen = 10, sentCommands = sentCommands)
        val device = GlowieDeviceImpl(transport)

        device.runScript("abcdefghijkl")

        assertEquals(3, sentCommands.size)
        assertEquals("run abcde\\", sentCommands[0])
        assertEquals(10, sentCommands[0].length)
        assertEquals("run fghij\\", sentCommands[1])
        assertEquals(10, sentCommands[1].length)
        assertEquals("run kl", sentCommands[2])
    }

    @Test
    fun runScript_largerMaxCommandLen() = runTest {
        val sentCommands = mutableListOf<String>()
        val transport = createTransport(maxCommandLen = 20, sentCommands = sentCommands)
        val device = GlowieDeviceImpl(transport)

        // 17 chars > 16 → should split into 2 chunks
        val script = "a".repeat(17)
        device.runScript(script)

        assertEquals(2, sentCommands.size)
        // First chunk: 15 chars + "\" = 15 + 1, plus "run " = 4 + 15 + 1 = 20
        assertEquals("run " + "a".repeat(15) + "\\", sentCommands[0])
        assertEquals(20, sentCommands[0].length)
        // Last chunk: 2 chars
        assertEquals("run " + "a".repeat(2), sentCommands[1])
    }
}
