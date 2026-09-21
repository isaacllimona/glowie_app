package glowie.glowie_app.launcher.presentation

import glowie.glowie_app.scripts.domain.ScriptHeader

data class ScriptListUIState (
    val scripts: List<ScriptHeader>,
    val isRefreshing: Boolean,

    val onScriptClick: (ScriptHeader) -> Unit,
    val onRefresh: () -> Unit,
)