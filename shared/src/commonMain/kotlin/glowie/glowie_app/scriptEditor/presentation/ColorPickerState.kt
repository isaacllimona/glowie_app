package glowie.glowie_app.scriptEditor.presentation

data class ColorPickerState (
    val hue: Float,
    val saturation: Float,
    val value: Float,

    val copiableColorString: String
)
