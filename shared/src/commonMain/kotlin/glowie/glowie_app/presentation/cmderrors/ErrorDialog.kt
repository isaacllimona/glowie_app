package glowie.glowie_app.presentation.cmderrors

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ErrorDialog(
    viewModelFactory: () -> ErrorViewModel,
    modifier: Modifier = Modifier,
) {
    val viewModel = viewModel { viewModelFactory() }
    val currentError by viewModel.currentError.collectAsStateWithLifecycle()

    val error = currentError ?: return

    AlertDialog(
        onDismissRequest = { viewModel.dismiss() },
        title = { Text("Command error") },
        text = {
            val deviceNames = error.devices.joinToString()
            Text("From $deviceNames: ${error.message}")
        },
        confirmButton = {
            TextButton(onClick = { viewModel.dismiss() }) {
                Text("OK")
            }
        },
        modifier = modifier,
    )
}
