package glowie.glowie_app.scriptEditor.presentation

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import glowie.glowie_app.scriptEditor.domain.GetScriptContent
import glowie.glowie_app.scriptEditor.domain.RunCustomScript
import glowie.glowie_app.scriptEditor.domain.ColorLivePreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


class EditorViewModel(
    private val runCustomScript: RunCustomScript,
    private val getScriptContent: GetScriptContent,
    private val colorLivePreview: ColorLivePreview,
    private val scriptId: String = "",
) : ViewModel() {
    private val _scriptText = MutableStateFlow("")
    val scriptText: StateFlow<String> = _scriptText.asStateFlow()

    private val _showColorPicker = MutableStateFlow(false)
    val showColorPicker: StateFlow<Boolean> = _showColorPicker.asStateFlow()

    private val _colorPickerState = MutableStateFlow(ColorPickerState(0f, 0f, 0f, "", ""))
    val colorPickerState: StateFlow<ColorPickerState> = _colorPickerState

    init {
        if (scriptId.isNotEmpty()) {
            viewModelScope.launch {
                _scriptText.value = getScriptContent(scriptId)
            }
        }
    }

    fun onScriptTextChange(text: String) {
        _scriptText.value = text
    }

    fun onLaunch() {
        viewModelScope.launch {
            runCustomScript(_scriptText.value)
        }
    }

    // ---- Color picker ----

    fun onToggleColorPicker() {
        _showColorPicker.value = !_showColorPicker.value
    }

    private fun makeColorStringHSV(hue: Float, saturation: Float, value: Float) : String {
        val deviceHue = (hue / 2f).roundToInt()
        val deviceSat = (saturation * 255f).roundToInt()
        val deviceVal = (value * 255f).roundToInt()
        return "h=$deviceHue s=$deviceSat v=$deviceVal"
    }

    private fun makeColorStringRGB(r: Int, g: Int, b: Int) : String = "r=$r g=$g b=$b"

    fun onColorChange(hue: Float, saturation: Float, value: Float) {
        val color = Color.hsv(hue, saturation, value)
        val r = (color.red * 255).roundToInt()
        val g = (color.green * 255).roundToInt()
        val b = (color.blue * 255).roundToInt()

        val copiableStringHSV = makeColorStringHSV(hue, saturation, value)
        val copiableStringRGB = makeColorStringRGB(r, g, b)

        _colorPickerState.value = ColorPickerState(
            hue, saturation, value,
            copiableStringHSV, copiableStringRGB)
        colorLivePreview.setColor(r, g, b)
    }

    suspend fun runColorLivePreview() {
        colorLivePreview.runLivePreview()
    }
}
