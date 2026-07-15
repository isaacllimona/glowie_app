package glowie.glowie_app.scriptEditor.domain

import glowie.glowie_app.launcher.domain.LaunchScript

class RunCustomScript(
    private val launchScript: LaunchScript,
) {
    suspend operator fun invoke(script: String) {
        launchScript(script)
    }
}
