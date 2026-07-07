package glowie.glowie_app.scriptEditor.domain

import glowie.glowie_app.launcher.domain.ScriptRepository

class GetScriptContent(
    private val scriptRepository: ScriptRepository,
) {
    suspend operator fun invoke(id: String): String {
        return scriptRepository.getScriptContent(id)
    }
}
