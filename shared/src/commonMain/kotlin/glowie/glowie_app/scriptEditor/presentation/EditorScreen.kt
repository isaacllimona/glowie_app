package glowie.glowie_app.scriptEditor.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun EditorScreen(
    viewModelFactory: () -> EditorViewModel,
    onBackClick: () -> Unit,
) {
    val viewModel = viewModel { viewModelFactory() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    EditorScreen(
        uiState = uiState,
        onBackClick = onBackClick,
    )
}

@Composable
fun EditorScreen(
    uiState: EditorUIState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            TextButton(onClick = onBackClick) {
                Text("← Back")
            }

            Spacer(Modifier.weight(1f))

            Button(onClick = uiState.onColorPickerClick) {
                Text("Color")
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(onClick = uiState.onSaveClick) {
                Text("Save")
            }
        }

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = uiState.scriptText,
            onValueChange = uiState.onScriptTextChange,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodyMedium,
            placeholder = { Text("Enter script code...") },
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = uiState.onLaunchClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Launch")
        }
    }

    when (uiState.dialog) {
        is EditorDialogUIState.Save -> SaveDialog(uiState.dialog.state)
        is EditorDialogUIState.ColorPicker -> ColorPickerDialog(uiState.dialog.state)
        is EditorDialogUIState.Error -> ErrorDialog(uiState.dialog.state)
        EditorDialogUIState.None -> Unit
    }
}

@Composable
private fun SaveDialog(
    uiState: SaveDialogUIState
) {
    val name = uiState.scriptName

    AlertDialog(
        onDismissRequest = uiState.onSaveDismiss,
        title = { Text("Save script") },
        text = { Text("Name: $name") },
        confirmButton = {
            TextButton(onClick = uiState.onSaveConfirm) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = uiState.onSaveDismiss) { Text("Cancel") }
        },
    )
}

@Composable
private fun ErrorDialog(
    uiState: ErrorDialogUIState
) {
    AlertDialog(
        onDismissRequest = uiState.onDismiss,
        title = { Text("Error") },
        text = { Text(uiState.msg) },
        confirmButton = { TextButton(onClick = uiState.onDismiss){ Text("OK") } },
    )
}
