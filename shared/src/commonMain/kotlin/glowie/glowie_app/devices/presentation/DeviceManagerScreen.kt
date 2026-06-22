package glowie.glowie_app.devices.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import glowie.glowie_app.comms.`interface`.GlowieDevice

@Composable
fun DeviceManagerScreen(
    viewModelFactory: () -> DeviceManagerViewModel,
    onBackClick: () -> Unit,
) {
    val viewModel = viewModel { viewModelFactory() }
    val devices by viewModel.devices.collectAsStateWithLifecycle()

    DeviceManagerScreen(
        devices = devices,
        onDisconnectAll = viewModel::disconnectAll,
        onShutdownAll = viewModel::shutdownAll,
        onBackClick = onBackClick,
    )
}

@Composable
fun DeviceManagerScreen(
    devices: List<GlowieDevice>,
    onDisconnectAll: () -> Unit,
    onShutdownAll: () -> Unit,
    onBackClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
    ) {
        TextButton(onClick = onBackClick) {
            Text("← Back")
        }

        if (devices.isEmpty()) {
            Text(
                text = "No devices connected",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 16.dp),
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(devices) { device ->
                    DeviceItem(device)
                    HorizontalDivider()
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Button(
                onClick = onDisconnectAll,
                modifier = Modifier.weight(1f),
            ) {
                Text("Disconnect all")
            }
            Button(
                onClick = onShutdownAll,
                modifier = Modifier.weight(1f),
            ) {
                Text("Shutdown all")
            }
        }
    }
}

@Composable
private fun DeviceItem(device: GlowieDevice) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = device.info.name ?: "Unnamed device",
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = device.info.id,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
