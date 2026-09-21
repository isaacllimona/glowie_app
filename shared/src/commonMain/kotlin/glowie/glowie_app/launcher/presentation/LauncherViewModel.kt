package glowie.glowie_app.launcher.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import glowie.glowie_app.launcher.domain.LaunchScript
import glowie.glowie_app.scripts.domain.ScriptHeader
import glowie.glowie_app.scripts.domain.ScriptRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LauncherViewModel(
    private val scriptRepository: ScriptRepository,
    private val launchScript: LaunchScript,
) : ViewModel() {
    private val _scripts = MutableStateFlow<List<ScriptHeader>>(emptyList())
    val scripts = _scripts.asStateFlow()

    private val _runningScript = MutableStateFlow<ScriptHeader?>(null)
    val runningScript = _runningScript.asStateFlow()

    private val _isScriptListRefreshing = MutableStateFlow(false)
    val isScriptListRefreshing = _isScriptListRefreshing.asStateFlow()

    init {
        viewModelScope.launch {
            _scripts.value = scriptRepository.listScripts()
        }
    }

    fun onScriptListRefresh() {
        _isScriptListRefreshing.value = true
        viewModelScope.launch {
            _scripts.value = scriptRepository.listScripts()
            _isScriptListRefreshing.value = false
        }
    }

    fun onScriptClick(script: ScriptHeader) {
        viewModelScope.launch {
            _runningScript.value = script
            val scriptContent = scriptRepository.getScriptContent(script.id)
            launchScript(scriptContent)
        }
    }

    fun stopRunningScript() {
        viewModelScope.launch { launchScript.stop() }
        _runningScript.value = null
    }
}
