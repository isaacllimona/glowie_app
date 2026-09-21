package glowie.glowie_app.launcher.presentation

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import glowie.glowie_app.scripts.domain.ScriptId

@Composable
fun LauncherScreen(
    viewModelFactory: () -> LauncherViewModel,
    onNewScriptClick: () -> Unit,
    onEditScript: (scriptId: ScriptId) -> Unit,
    modifier: Modifier = Modifier,
) {
    val launcherViewModel = viewModel { viewModelFactory() }
    val scriptListUIState by launcherViewModel.scriptListUIState.collectAsStateWithLifecycle()
    val runningScriptUIState by launcherViewModel.runningScriptUIState.collectAsStateWithLifecycle()

    LauncherScreen(
        scriptListUIState = scriptListUIState,
        runningScriptUIState = runningScriptUIState,

        modifier = modifier,

        onNewScriptClick = onNewScriptClick,
        onEditScript = onEditScript,
    )
}

@Composable
fun LauncherScreen(
    scriptListUIState: ScriptListUIState,
    runningScriptUIState: RunningScriptUIState,

    modifier: Modifier = Modifier,
    onNewScriptClick: () -> Unit = {},
    onEditScript: (scriptId: ScriptId) -> Unit = {},
) {
    Column(modifier = modifier.fillMaxWidth()) {

        if (runningScriptUIState is RunningScriptUIState.Running) {
            RunningScriptSurface(
                uiState = runningScriptUIState
            )
        }

        ScriptList(
            uiState = scriptListUIState,
            onEditScript = onEditScript,
            modifier = modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        )

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

    if (scriptListUIState.dialogState is ScriptDialogUIState.DeleteScript) {
        DeleteScriptDialog(scriptListUIState.dialogState)
    }
}

@Composable
fun ScriptList(
    uiState: ScriptListUIState,
    onEditScript: (scriptId: ScriptId) -> Unit,
    modifier: Modifier
) {
    PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        onRefresh = uiState.onRefresh,
        modifier = modifier
    ) {
        LazyColumn() {
            items(uiState.scripts) { script ->
                Column {
                    Text(
                        text = script.metadata.name,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .combinedClickable(
                                onClick = { uiState.onScriptClick(script) },
                                onLongClick = { uiState.onScriptLongClick(script) },
                            )
                            .padding(vertical = 12.dp),
                    )

                    DropdownMenu(
                        expanded = uiState.dropdownState.expandedScript == script,
                        onDismissRequest = uiState.dropdownState.onDismiss,
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = {
                                uiState.dropdownState.onDismiss()
                                onEditScript(script.id)
                            },
                        )
                        if (uiState.dropdownState.showDelete) {
                            DropdownMenuItem(
                                text = { Text("Delete") },
                                onClick = uiState.dropdownState.onDelete
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteScriptDialog(
    uiState: ScriptDialogUIState.DeleteScript
) {
    AlertDialog(
        onDismissRequest = uiState.onDismiss,
        title = { Text("Delete script") },
        text = { Text("Delete \"${uiState.script.metadata.name}\"?") },
        confirmButton = {
            TextButton(onClick = uiState.onConfirm) { Text("Delete") }
        },
        dismissButton = {
            TextButton(onClick = uiState.onDismiss) { Text("Cancel") }
        },
    )
}

@Composable
fun RunningScriptSurface(
    uiState: RunningScriptUIState.Running,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.secondaryContainer,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Running: ${uiState.scriptName}",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            TextButton(onClick = uiState.onStop) {
                Text("Stop")
            }
        }
    }
}

