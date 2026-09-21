package glowie.glowie_app.launcher.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import glowie.glowie_app.launcher.domain.LaunchScript
import glowie.glowie_app.scripts.domain.ScriptHeader
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

        onScriptClick = ::onScriptClick,
        onRefresh = ::onScriptListRefresh
    ))
    val scriptListUIState = _scriptListUIState.asStateFlow()

    private val _runningScriptUIState = MutableStateFlow<RunningScriptUIState>(RunningScriptUIState.None)
    val runningScriptUIState = _runningScriptUIState.asStateFlow()

    init {
        viewModelScope.launch {
            val newScriptList = scriptRepository.listScripts()
            _scriptListUIState.update { it.copy(scripts = newScriptList) }
        }
    }

    private fun onScriptListRefresh() {
        _scriptListUIState.update { it.copy(isRefreshing = true) }
        viewModelScope.launch {
            val newScriptList = scriptRepository.listScripts()
            _scriptListUIState.update { it.copy(scripts = newScriptList, isRefreshing = false) }
        }
    }

    private fun onScriptClick(script: ScriptHeader) {
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
}
