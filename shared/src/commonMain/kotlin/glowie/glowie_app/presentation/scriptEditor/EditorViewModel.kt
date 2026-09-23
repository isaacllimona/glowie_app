package glowie.glowie_app.presentation.scriptEditor

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import glowie.glowie_app.core.devices.control.ColorLivePreview
import glowie.glowie_app.core.devices.control.LaunchScript
import glowie.glowie_app.core.scripts.services.SaveStatus
import glowie.glowie_app.core.scripts.services.SaveScript
import glowie.glowie_app.core.scripts.objects.ScriptId
import glowie.glowie_app.core.scripts.objects.ScriptOrigin
import glowie.glowie_app.core.scripts.repositories.ScriptRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class EditorViewModel(
    private val scriptRepository: ScriptRepository,
    private val launchScript: LaunchScript,
    private val colorLivePreview: ColorLivePreview,
    private val saveScript: SaveScript,
) : ViewModel() {
    private val _uiState = MutableStateFlow(EditorUIState(
        scriptText = "",
        dialog = EditorDialogUIState.None,
        onScriptTextChange = ::changeScriptText,
        onSaveClick = ::saveScript,
        onColorPickerClick = ::showColorPicker,
        onLaunchClick = ::launchScript,
    ))
    val uiState = _uiState.asStateFlow()

    private var currentScriptId: ScriptId? = null

    fun openScript(scriptId: ScriptId) {
        viewModelScope.launch {
            changeScriptText(scriptRepository.getScriptContent(scriptId))
        }
        currentScriptId = scriptId
    }

    fun newScript() {
        changeScriptText("")
        currentScriptId = null
    }

    private fun changeScriptText(text: String) {
        _uiState.update { it.copy(scriptText = text) }
    }

    private fun launchScript() {
        viewModelScope.launch {
            launchScript(uiState.value.scriptText)
        }
    }

    private fun clearDialogs() {
        _uiState.update{ it.copy(dialog = EditorDialogUIState.None) }
    }

    // ---- Script save dialog ----

    private fun saveScript() {
        val saveStatus = saveScript.prepareSave(uiState.value.scriptText, ScriptOrigin.User)
        when (saveStatus) {
            is SaveStatus.Invalid -> showError(saveStatus.msg)
            is SaveStatus.Ready -> _uiState.update {
                it.copy(
                    dialog = EditorDialogUIState.Save(
                        state = SaveDialogUIState(
                            scriptName = saveStatus.payload.metadata.name,
                            onSaveConfirm = { onSaveConfirm(saveStatus) },
                            onSaveDismiss = ::clearDialogs
                        )
                    )
                )
            }
        }
    }

    private fun onSaveConfirm(saveStatus: SaveStatus.Ready) {
        viewModelScope.launch {
            try {
                saveScript.save(saveStatus)
            } catch (e: Exception) {
                showError("Error saving script: ${e.message}")
                return@launch
            } finally {
                clearDialogs()
            }
            currentScriptId = saveStatus.payload.id
        }
    }

    // ---- Error message ----

    private fun showError(message: String) {
        _uiState.update {
            it.copy(
                dialog = EditorDialogUIState.Error(
                    state = ErrorDialogUIState(
                        msg = message,
                        onDismiss = ::clearDialogs
                    )
                )
            )
        }
    }

    // ---- Color picker ----

    private var colorPickerHSV = HSV(0f, 0f, 1f)

    private fun showColorPicker() {
        val hsv = colorPickerHSV

        val color = Color.hsv(hsv.hue, hsv.saturation, hsv.value)
        val r = (color.red * 255).roundToInt()
        val g = (color.green * 255).roundToInt()
        val b = (color.blue * 255).roundToInt()

        val copiableStringHSV = makeColorStringHSV(hsv)
        val copiableStringRGB = makeColorStringRGB(r, g, b)

        _uiState.update {
            it.copy(
                dialog = EditorDialogUIState.ColorPicker(
                    state = ColorPickerDialogUIState(
                        hsv = hsv,
                        copiableColorStringHSV = copiableStringHSV,
                        copiableColorStringRGB = copiableStringRGB,

                        onDismiss = ::clearDialogs,
                        onColorChange = { hsv ->
                            colorPickerHSV = hsv
                            showColorPicker()
                        },
                        runColorLivePreview = colorLivePreview::runLivePreview
                    )
                )
            )
        }

        colorLivePreview.setColor(r, g, b)
    }

    private fun makeColorStringHSV(hsv: HSV) : String {
        val deviceHue = (hsv.hue / 2f).roundToInt()
        val deviceSat = (hsv.saturation * 255f).roundToInt()
        val deviceVal = (hsv.value * 255f).roundToInt()
        return "h=$deviceHue s=$deviceSat v=$deviceVal"
    }

    private fun makeColorStringRGB(r: Int, g: Int, b: Int) : String = "r=$r g=$g b=$b"
}
