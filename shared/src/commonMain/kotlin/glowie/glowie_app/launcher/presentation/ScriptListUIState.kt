package glowie.glowie_app.launcher.presentation

import glowie.glowie_app.scripts.domain.ScriptHeader

sealed interface ScriptDialogUIState {
    object None : ScriptDialogUIState
    data class DeleteScript(
        val script: ScriptHeader,
        val onConfirm: () -> Unit,
        val onDismiss: () -> Unit,
    ) : ScriptDialogUIState
}

data class ScriptDropdownUIState (
    val expandedScript: ScriptHeader?,
    val showDelete: Boolean,
    val onDismiss: () -> Unit,
    val onDelete: () -> Unit,
)

data class ScriptListUIState (
    val scripts: List<ScriptHeader>,
    val isRefreshing: Boolean,
    val dropdownState: ScriptDropdownUIState,
    val dialogState: ScriptDialogUIState,

    val onRefresh: () -> Unit,
    val onScriptClick: (ScriptHeader) -> Unit,
    val onScriptLongClick: (ScriptHeader) -> Unit,
)