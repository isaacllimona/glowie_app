package glowie.glowie_app.common

object scriptBits {
    val stopAndClear = "led.setColor(0,0,0) stop()"
    fun setColor(r: Int, g: Int, b: Int) = "led.setColor($r, $g, $b)"
}