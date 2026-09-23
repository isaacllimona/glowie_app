package glowie.glowie_app.presentation.launcher

import glowie.glowie_app.core.scripts.objects.ScriptHeader

sealed interface ScriptListDialogUIState {
    object None : ScriptListDialogUIState
    data class DeleteScript(
        val scriptName: String,
        val onConfirm: () -> Unit,
        val onDismiss: () -> Unit,
    ) : ScriptListDialogUIState
}

data class ScriptUIState (
    val scriptHeader : ScriptHeader,
    val showDelete: Boolean,

    val onLaunch: () -> Unit,
    val onDelete: () -> Unit,
)

data class ScriptListUIState (
    val scripts: List<ScriptUIState>,
    val isRefreshing: Boolean,
    val dialogState: ScriptListDialogUIState,

    val onRefresh: () -> Unit,
)