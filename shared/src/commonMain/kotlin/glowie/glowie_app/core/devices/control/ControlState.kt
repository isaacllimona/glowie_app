package glowie.glowie_app.core.devices.control

import kotlinx.coroutines.flow.MutableStateFlow

class ControlState {
    // enabled and brightness are set through a conflated flow so that intermediate values are skipped
    // if multiple changes occur before the last one has been sent to the devices
    internal val enabledFlow = MutableStateFlow(true)
    internal val brightnessFlow = MutableStateFlow(1f)

    var enabled: Boolean
        set(value) { enabledFlow.value = value }
        get() = enabledFlow.value

    var brightness: Float
        set(value) { brightnessFlow.value = value }
        get() = brightnessFlow.value
}
