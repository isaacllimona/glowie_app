package glowie.glowie_app.scriptEditor.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

    val scriptText by viewModel.scriptText.collectAsStateWithLifecycle()
    val showColorPicker by viewModel.showColorPicker.collectAsStateWithLifecycle()
    val colorPickerState by viewModel.colorPickerState.collectAsStateWithLifecycle()

    EditorScreen(
        scriptText = scriptText,
        onScriptTextChange = viewModel::onScriptTextChange,
        onLaunch = viewModel::onLaunch,
        onBackClick = onBackClick,
        showColorPicker = showColorPicker,
        colorPickerState = colorPickerState,
        onToggleColorPicker = viewModel::onToggleColorPicker,
        onColorChange = viewModel::onColorChange,
        runColorLivePreview = viewModel::runColorLivePreview,
    )
}

@Composable
fun EditorScreen(
    scriptText: String,
    onScriptTextChange: (String) -> Unit,
    onLaunch: () -> Unit,
    onBackClick: () -> Unit,

    showColorPicker: Boolean,
    colorPickerState: ColorPickerState,
    onToggleColorPicker: () -> Unit,
    onColorChange: (hue: Float, saturation: Float, value: Float) -> Unit,
    runColorLivePreview: suspend () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            TextButton(onClick = onBackClick) {
                Text("← Back")
            }

            Spacer(Modifier.weight(1f))

            Button(onClick = onToggleColorPicker) {
                Text("Color")
            }
        }

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = scriptText,
            onValueChange = onScriptTextChange,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodyMedium,
            placeholder = { Text("Enter script code...") },
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onLaunch,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Launch")
        }
    }

    if (showColorPicker) {
        LaunchedEffect(runColorLivePreview) {
            runColorLivePreview()
        }

        ColorPickerDialog(
            colorPickerState = colorPickerState,
            onColorChange = onColorChange,
            onDismissRequest = onToggleColorPicker
        )
    }
}
