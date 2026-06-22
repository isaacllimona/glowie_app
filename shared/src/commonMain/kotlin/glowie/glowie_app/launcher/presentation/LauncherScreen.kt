package glowie.glowie_app.launcher.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
) {
    val launcherViewModel = viewModel { viewModelFactory() }
    val scripts by launcherViewModel.scripts.collectAsStateWithLifecycle()

    LauncherScreen(
        scripts = scripts,
        onScriptClick = launcherViewModel::onScriptClick,
        onNewScriptClick = onNewScriptClick,
        modifier = modifier,
    )
}

@Composable
fun LauncherScreen(
    scripts: List<Script>,
    onScriptClick: (String) -> Unit = {},
    onNewScriptClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalDivider()

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        ) {
            items(scripts) { script ->
                Text(
                    text = script.name,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onScriptClick(script.id) }
                        .padding(vertical = 12.dp),
                )
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
