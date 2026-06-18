package glowie.glowie_app.control.presentation

import androidx.lifecycle.ViewModel
import glowie.glowie_app.control.domain.Control
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

const val INIT_BRIGHTNESS: Float = 1f

class ControlViewModel (
    val controlDomain: Control
) : ViewModel() {
    private val _isOn = MutableStateFlow(false)
    val isOn: StateFlow<Boolean> = _isOn.asStateFlow()

    private val _brightness = MutableStateFlow(INIT_BRIGHTNESS)
    val brightness: StateFlow<Float> = _brightness.asStateFlow()

    fun onEnableButtonClick() {
        _isOn.value = !_isOn.value
        controlDomain.setEnabled(isOn.value)
    }

    fun onBrightnessChange(value: Float) {
        _brightness.value = value
        controlDomain.setBrightness(value)
    }
}
