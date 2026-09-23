package glowie.glowie_app.presentation.launcher

sealed class RunningScriptUIState {
    object None : RunningScriptUIState()
    data class Running(
        val scriptName: String,
        val onStop: () -> Unit,
    ) : RunningScriptUIState()
}
