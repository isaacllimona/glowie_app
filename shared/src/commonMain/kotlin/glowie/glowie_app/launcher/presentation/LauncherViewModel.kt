package glowie.glowie_app.launcher.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import glowie.glowie_app.launcher.domain.LaunchScript
import glowie.glowie_app.scripts.domain.ScriptHeader
import glowie.glowie_app.scripts.domain.ScriptOrigin
import glowie.glowie_app.scripts.domain.ScriptRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LauncherViewModel(
    private val scriptRepository: ScriptRepository,
    private val launchScript: LaunchScript,
) : ViewModel() {
    private val _scriptListUIState = MutableStateFlow(ScriptListUIState(
        scripts = emptyList(),
        isRefreshing = false,
        dialogState = ScriptListDialogUIState.None,
        onRefresh = ::refreshScriptList,
    ))
    val scriptListUIState = _scriptListUIState.asStateFlow()

    private val _runningScriptUIState = MutableStateFlow<RunningScriptUIState>(RunningScriptUIState.None)
    val runningScriptUIState = _runningScriptUIState.asStateFlow()

    init {
        refreshScriptList()
    }

    private fun refreshScriptList() {
        _scriptListUIState.update { it.copy(isRefreshing = true) }
        viewModelScope.launch {
            val newScriptList = scriptRepository.listScripts()
                .map { scriptHeader ->
                    ScriptUIState(
                        scriptHeader = scriptHeader,
                        showDelete = showDeleteForScript(scriptHeader),
                        onLaunch = { launchScript(scriptHeader) },
                        onDelete = { requestScriptDelete(scriptHeader) },
                    )
                }
            _scriptListUIState.update { it.copy(scripts = newScriptList, isRefreshing = false) }
        }
    }

    private fun launchScript(script: ScriptHeader) {
        viewModelScope.launch {
            val scriptContent = scriptRepository.getScriptContent(script.id)
            launchScript(scriptContent)
            _runningScriptUIState.value = RunningScriptUIState.Running(scriptName = script.metadata.name, onStop = ::stopRunningScript)
        }
    }

    private fun stopRunningScript() {
        viewModelScope.launch { launchScript.stop() }
        _runningScriptUIState.value = RunningScriptUIState.None
    }

    private fun showDeleteForScript(script: ScriptHeader): Boolean =
        script.metadata.origin == ScriptOrigin.User

    private fun requestScriptDelete(script: ScriptHeader) {
        _scriptListUIState.update {
            it.copy(
                dialogState = ScriptListDialogUIState.DeleteScript(
                    scriptName = script.metadata.name,
                    onConfirm = { confirmScriptDelete(script) },
                    onDismiss = ::hideDeleteDialog,
                )
            )
        }
    }

    private fun confirmScriptDelete(script: ScriptHeader) {
        viewModelScope.launch {
            scriptRepository.deleteScript(script.id)
            hideDeleteDialog()
            refreshScriptList()
        }
    }

    private fun hideDeleteDialog() {
        _scriptListUIState.update { it.copy(dialogState = ScriptListDialogUIState.None) }
    }
}
