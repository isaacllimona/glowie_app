package glowie.glowie_app.control.presentation

import androidx.lifecycle.ViewModel
import glowie.glowie_app.control.domain.ControlState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ControlViewModel (
    val controlState: ControlState
) : ViewModel() {
    private val _isOn = MutableStateFlow(controlState.enabled)
    val isOn: StateFlow<Boolean> = _isOn.asStateFlow()

    private val _brightness = MutableStateFlow(controlState.brightness)
    val brightness: StateFlow<Float> = _brightness.asStateFlow()

    fun onEnableButtonClick() {
        _isOn.value = !_isOn.value
        controlState.enabled = isOn.value
    }

    fun onBrightnessChange(value: Float) {
        _brightness.value = value
        controlState.brightness = value
    }
}
