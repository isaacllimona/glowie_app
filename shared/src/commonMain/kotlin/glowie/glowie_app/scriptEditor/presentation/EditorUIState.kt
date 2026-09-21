package glowie.glowie_app.scriptEditor.presentation

data class HSV(val hue: Float, val saturation: Float, val value: Float)

data class SaveDialogUIState(
    val scriptName: String,

    val onSaveConfirm: () -> Unit,
    val onSaveDismiss: () -> Unit,
)

data class ColorPickerDialogUIState(
    val hsv: HSV,
    val copiableColorStringHSV: String,
    val copiableColorStringRGB: String,

    val onDismiss: () -> Unit,
    val onColorChange: (HSV) -> Unit,
    val runColorLivePreview: suspend () -> Unit
)

data class ErrorDialogUIState(
    val msg: String,
    val onDismiss: () -> Unit
)

sealed interface EditorDialogUIState {
    object None : EditorDialogUIState
    data class Save(val state: SaveDialogUIState) : EditorDialogUIState
    data class ColorPicker(val state: ColorPickerDialogUIState) : EditorDialogUIState
    data class Error(val state: ErrorDialogUIState) : EditorDialogUIState
}

data class EditorUIState (
    val scriptText: String,
    val dialog: EditorDialogUIState,

    val onScriptTextChange: (String) -> Unit,
    val onSaveClick: () -> Unit,
    val onColorPickerClick: () -> Unit,
    val onLaunchClick: () -> Unit,
)
