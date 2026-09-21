package glowie.glowie_app.launcher.presentation

import glowie.glowie_app.scripts.domain.ScriptHeader

sealed class RunningScriptUIState {
    object None : RunningScriptUIState()
    data class Running(
        val scriptName: String,
        val onStop: () -> Unit,
    ) : RunningScriptUIState()
}
