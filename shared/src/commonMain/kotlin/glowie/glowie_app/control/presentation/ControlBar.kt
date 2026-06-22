package glowie.glowie_app.control.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ControlBar(
    viewModelFactory: () -> ControlViewModel
) {
    val viewModel = viewModel { viewModelFactory() }
    val isOn by viewModel.isOn.collectAsStateWithLifecycle()
    val brightness by viewModel.brightness.collectAsStateWithLifecycle()

    ControlBar(
        isOn = isOn,
        brightness = brightness,
        onEnableButtonClick = viewModel::onEnableButtonClick,
        onBrightnessChange = viewModel::onBrightnessChange,
    )
}

@Composable
fun ControlBar(
    isOn: Boolean,
    brightness: Float,
    onEnableButtonClick: () -> Unit,
    onBrightnessChange: (Float) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "${(brightness * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
            )

            Slider(
                value = brightness,
                onValueChange = onBrightnessChange,
                modifier = Modifier.weight(1f),
            )

            Spacer(Modifier.width(8.dp))

            Button(
                onClick = onEnableButtonClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isOn)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.surfaceVariant,
                ),
                contentPadding = PaddingValues(12.dp),
            ) {
                Text("☀", style = MaterialTheme.typography.headlineMedium)
            }
        }
    }
}

@Preview
@Composable
fun ControlBarPreview() {
    ControlBar(
        false, 1f,
        {}, {}
    )
}
