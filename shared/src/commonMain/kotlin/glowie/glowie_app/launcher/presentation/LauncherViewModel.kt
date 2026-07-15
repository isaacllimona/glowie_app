package glowie.glowie_app.launcher.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import glowie.glowie_app.launcher.domain.LaunchScriptFromID
import glowie.glowie_app.launcher.domain.Script
import glowie.glowie_app.launcher.domain.ScriptRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LauncherViewModel(
    private val scriptRepository: ScriptRepository,
    private val launchScriptFromID: LaunchScriptFromID,
) : ViewModel() {
    private val _scripts = MutableStateFlow<List<Script>>(emptyList())
    val scripts: StateFlow<List<Script>> = _scripts.asStateFlow()

    private val _runningScript = MutableStateFlow<Script?>(null)
    val runningScript: StateFlow<Script?> = _runningScript.asStateFlow()

    init {
        viewModelScope.launch {
            _scripts.value = scriptRepository.listScripts()
        }
    }

    fun onScriptClick(scriptId: String) {
        viewModelScope.launch {
            val script = _scripts.value.find { it.id == scriptId } ?: return@launch
            _runningScript.value = script
            launchScriptFromID(scriptId)
        }
    }

    fun stopRunningScript() {
        viewModelScope.launch { launchScriptFromID.stop() }
        _runningScript.value = null
    }
}
