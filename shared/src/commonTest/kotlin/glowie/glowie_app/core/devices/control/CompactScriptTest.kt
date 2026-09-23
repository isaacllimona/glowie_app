package glowie.glowie_app.core.devices.control

import kotlin.test.Test
import kotlin.test.assertEquals

class CompactScriptTest {
    @Test
    fun compactScript() {
        val input = """
                a = 0 -- a comment
            b = 1--also a comment
            c = 2--[[multi--
            line comment]]d=3
            
            -- more comments
            
                     e = 4
            print ( " i like turtles " )
        """.trimIndent()

        val expected = """a=0 b=1 c=2 d=3 e=4 print(" i like turtles ")"""
        
        val compacted = glowie.glowie_app.core.devices.control.compactScript(input)
        
        assertEquals(expected, compacted)
    }
}