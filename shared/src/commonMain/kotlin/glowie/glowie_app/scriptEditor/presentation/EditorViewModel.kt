package glowie.glowie_app.scriptEditor.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import glowie.glowie_app.scriptEditor.domain.GetScriptContent
import glowie.glowie_app.scriptEditor.domain.RunCustomScript
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditorViewModel(
    private val runCustomScript: RunCustomScript,
    private val getScriptContent: GetScriptContent,
    private val scriptId: String = "",
) : ViewModel() {
    private val _scriptText = MutableStateFlow("")
    val scriptText: StateFlow<String> = _scriptText.asStateFlow()

    init {
        if (scriptId.isNotEmpty()) {
            viewModelScope.launch {
                _scriptText.value = getScriptContent(scriptId)
            }
        }
    }

    fun onScriptTextChange(text: String) {
        _scriptText.value = text
    }

    fun onLaunch() {
        viewModelScope.launch {
            runCustomScript(_scriptText.value)
        }
    }
}
