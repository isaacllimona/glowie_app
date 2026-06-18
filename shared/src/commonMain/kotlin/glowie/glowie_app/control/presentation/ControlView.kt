package glowie.glowie_app.control.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ControlView(
    viewModelFactory: () -> ControlViewModel
) {
    val viewModel = viewModel { viewModelFactory() }
    val isOn by viewModel.isOn.collectAsStateWithLifecycle()
    val brightness by viewModel.brightness.collectAsStateWithLifecycle()

    ControlView(
        isOn = isOn,
        brightness = brightness,
        onEnableButtonClick = viewModel::onEnableButtonClick,
        onBrightnessChange = viewModel::onBrightnessChange,
    )
}

@Composable
fun ControlView(
    isOn: Boolean,
    brightness: Float,
    onEnableButtonClick: () -> Unit,
    onBrightnessChange: (Float) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = "Brightness: ${(brightness * 100).toInt()}%")

        Slider(
            value = brightness,
            onValueChange = { onBrightnessChange(it) },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { onEnableButtonClick() },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isOn)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.surfaceVariant,
            ),
        ) {
            Text("☀", style = MaterialTheme.typography.headlineMedium)
        }
    }
}

@Preview
@Composable
fun ControlViewPreview() {
    ControlView(
        false, 1f,
        {}, {}
    )
}
