package glowie.glowie_app.scriptEditor.presentation

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import glowie.glowie_app.scriptEditor.domain.GetScriptContent
import glowie.glowie_app.scriptEditor.domain.RunCustomScript
import glowie.glowie_app.scriptEditor.domain.ColorLivePreview
import glowie.glowie_app.scripts.domain.SaveStatus
import glowie.glowie_app.scripts.domain.SaveScript
import glowie.glowie_app.scripts.domain.ScriptId
import glowie.glowie_app.scripts.domain.ScriptOrigin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

sealed class SaveDialogState {
    object Hide : SaveDialogState()
    class Show (
        val saveStatus: SaveStatus.Ready
    ) : SaveDialogState()
}

class EditorViewModel(
    private val runCustomScript: RunCustomScript,
    private val getScriptContent: GetScriptContent,
    private val colorLivePreview: ColorLivePreview,
    private val saveScript: SaveScript,
) : ViewModel() {
    private val _scriptText = MutableStateFlow("")
    val scriptText = _scriptText.asStateFlow()

    private val _showColorPicker = MutableStateFlow(false)
    val showColorPicker = _showColorPicker.asStateFlow()

    private val _colorPickerState = MutableStateFlow(ColorPickerState(0f, 0f, 0f, "", ""))
    val colorPickerState = _colorPickerState.asStateFlow()

    private val _saveDialogState = MutableStateFlow<SaveDialogState>(SaveDialogState.Hide)
    val saveDialogState = _saveDialogState.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private var currentScriptId: ScriptId? = null

    fun openScript(scriptId: ScriptId) {
        viewModelScope.launch {
            _scriptText.value = getScriptContent(scriptId)
        }
        currentScriptId = scriptId
    }

    fun newScript() {
        _scriptText.value = ""
        currentScriptId = null
    }

    fun onScriptTextChange(text: String) {
        _scriptText.value = text
    }

    fun onLaunch() {
        viewModelScope.launch {
            runCustomScript(_scriptText.value)
        }
    }

    // ---- Script saving ----

    fun onSaveClick() {
        val saveStatus = saveScript.prepareSave(scriptText.value, ScriptOrigin.User)
        when (saveStatus) {
            is SaveStatus.Invalid -> showError(saveStatus.msg)
            is SaveStatus.Ready -> _saveDialogState.value = SaveDialogState.Show(saveStatus = saveStatus)
        }
    }

    fun onSaveDismiss() {
        _saveDialogState.value = SaveDialogState.Hide
    }

    fun onSaveConfirm() {
        val saveStatus = (saveDialogState.value as SaveDialogState.Show).saveStatus
        viewModelScope.launch {
            try {
                saveScript.save(saveStatus)
            } catch (e: Exception) {
                showError("Error saving script: ${e.message}")
                return@launch
            } finally {
                _saveDialogState.value = SaveDialogState.Hide
            }
            currentScriptId = saveStatus.payload.id
        }
    }

    // ---- Error message ----
    private fun showError(message: String) { _errorMessage.value = message }

    fun onErrorDismiss() {
        _errorMessage.value = null
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
