package glowie.glowie_app.core.devices.control

object scriptBits {
    val stopAndClear = "led.setColor(0,0,0) stop()"
    fun setColor(r: Int, g: Int, b: Int) = "led.setColor($r, $g, $b)"
}