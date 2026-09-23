package glowie.glowie_app.presentation.cmderrors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import glowie.glowie_app.core.devices.monitoring.CmdError
import glowie.glowie_app.core.devices.monitoring.MergedCmdErrFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ErrorViewModel(
    private val mergedCmdErrFlow: MergedCmdErrFlow,
) : ViewModel() {
    private var _currentError: CmdError? = null
    private val _errorQueue = ArrayDeque<CmdError>()

    private val _currentErrorFlow = MutableStateFlow<CmdError?>(null)
    val currentError: StateFlow<CmdError?> = _currentErrorFlow.asStateFlow()

    init {
        viewModelScope.launch { mergedCmdErrFlow.run() }
        viewModelScope.launch {
            mergedCmdErrFlow.errors.collect { (deviceName, message) ->
                val current = _currentError
                if (current == null) {
                    _currentError = CmdError(devices = listOf(deviceName), message = message)
                    _currentErrorFlow.value = _currentError
                } else if (current.message == message) {
                    _currentError = current.copy(devices = current.devices + deviceName)
                    _currentErrorFlow.value = _currentError
                } else {
                    _errorQueue.addLast(CmdError(devices = listOf(deviceName), message = message))
                }
            }
        }
    }

    fun dismiss() {
        if (_errorQueue.isNotEmpty()) {
            _currentError = _errorQueue.removeFirst()
        } else {
            _currentError = null
        }
        _currentErrorFlow.value = _currentError
    }
}
