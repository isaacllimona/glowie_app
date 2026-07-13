package glowie.glowie_app.comms.internal

import glowie.glowie_app.comms.`interface`.GlowieDeviceControl
import glowie.glowie_app.comms.internal.common.GlowieCommands
import glowie.glowie_app.comms.internal.common.GlowieDeviceCommandSender
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GlowieCommandsTest {

    private fun createGlowieCommands(
        maxCommandLen: Int,
        sentCommands: MutableList<String> = mutableListOf()
    ) : GlowieDeviceControl  {
        val commandSender = object : GlowieDeviceCommandSender{
            override suspend fun sendCommand(cmd: String) {
                sentCommands.add(cmd)
            }
            override suspend fun getMaxCommandLen(): Int = maxCommandLen
        }

        return GlowieCommands(commandSender)
    }

    @Test
    fun runScript_shortScriptSingleChunk() = runTest {
        val sentCommands = mutableListOf<String>()
        val commands = createGlowieCommands(maxCommandLen = 10, sentCommands = sentCommands)

        commands.runScript("abc")

        assertEquals(1, sentCommands.size)
        assertEquals("run abc", sentCommands[0])
        assertEquals(7, sentCommands[0].length)
    }

    @Test
    fun runScript_scriptExactlyMaxScriptLen() = runTest {
        val sentCommands = mutableListOf<String>()
        val commands = createGlowieCommands(maxCommandLen = 10, sentCommands = sentCommands)

        commands.runScript("abcdef")

        assertEquals(1, sentCommands.size)
        assertEquals("run abcdef", sentCommands[0])
    }

    @Test
    fun runScript_scriptOneOverMaxSplitsIntoTwoChunks() = runTest {
        val sentCommands = mutableListOf<String>()
        val commands = createGlowieCommands(maxCommandLen = 10, sentCommands = sentCommands)

        commands.runScript("abcdefg")

        assertEquals(2, sentCommands.size)
        assertEquals("run abcde\\", sentCommands[0])
        assertEquals(10, sentCommands[0].length)
        assertEquals("run fg", sentCommands[1])
    }

    @Test
    fun runScript_longScriptMultipleChunks() = runTest {
        val sentCommands = mutableListOf<String>()
        val commands = createGlowieCommands(maxCommandLen = 10, sentCommands = sentCommands)

        commands.runScript("abcdefghijkl")

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
        val commands = createGlowieCommands(maxCommandLen = 20, sentCommands = sentCommands)

        // 17 chars > 16 → should split into 2 chunks
        val script = "a".repeat(17)
        commands.runScript(script)

        assertEquals(2, sentCommands.size)
        // First chunk: 15 chars + "\" = 15 + 1, plus "run " = 4 + 15 + 1 = 20
        assertEquals("run " + "a".repeat(15) + "\\", sentCommands[0])
        assertEquals(20, sentCommands[0].length)
        // Last chunk: 2 chars
        assertEquals("run " + "a".repeat(2), sentCommands[1])
    }
}
