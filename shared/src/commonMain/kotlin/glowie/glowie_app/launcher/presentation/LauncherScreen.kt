package glowie.glowie_app.launcher.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import glowie.glowie_app.launcher.domain.Script

@Composable
fun LauncherScreen(
    viewModelFactory: () -> LauncherViewModel,
    onNewScriptClick: () -> Unit,
    modifier: Modifier = Modifier,
    onEditScript: (scriptId: String) -> Unit = {},
) {
    val launcherViewModel = viewModel { viewModelFactory() }
    val scripts by launcherViewModel.scripts.collectAsStateWithLifecycle()
    val runningScript by launcherViewModel.runningScript.collectAsStateWithLifecycle()

    LauncherScreen(
        scripts = scripts,
        runningScript = runningScript,
        onScriptClick = launcherViewModel::onScriptClick,
        onStopScript = launcherViewModel::stopRunningScript,
        onNewScriptClick = onNewScriptClick,
        onEditScript = onEditScript,
        modifier = modifier,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LauncherScreen(
    scripts: List<Script>,
    runningScript: Script? = null,
    onScriptClick: (String) -> Unit = {},
    onStopScript: () -> Unit = {},
    onNewScriptClick: () -> Unit = {},
    onEditScript: (scriptId: String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var expandedScriptId by remember { mutableStateOf<String?>(null) }

    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalDivider()

        if (runningScript != null) {
            Surface(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.secondaryContainer,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Running: ${runningScript.name}",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    TextButton(onClick = onStopScript) {
                        Text("Stop")
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        ) {
            items(scripts) { script ->
                Column {
                    Text(
                        text = script.name,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .combinedClickable(
                                onClick = { onScriptClick(script.id) },
                                onLongClick = { expandedScriptId = script.id },
                            )
                            .padding(vertical = 12.dp),
                    )

                    DropdownMenu(
                        expanded = expandedScriptId == script.id,
                        onDismissRequest = { expandedScriptId = null },
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = {
                                expandedScriptId = null
                                onEditScript(script.id)
                            },
                        )
                    }
                }
            }
        }

        HorizontalDivider()

        Button(
            onClick = onNewScriptClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Text("Custom script")
        }

        Spacer(Modifier.height(16.dp))
    }
}
