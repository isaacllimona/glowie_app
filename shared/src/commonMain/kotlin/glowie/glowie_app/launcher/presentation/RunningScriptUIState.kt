package glowie.glowie_app.launcher.presentation

sealed class RunningScriptUIState {
    object None : RunningScriptUIState()
    data class Running(
        val scriptName: String,
        val onStop: () -> Unit,
    ) : RunningScriptUIState()
}
