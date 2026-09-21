package glowie.glowie_app.scriptEditor.domain

import glowie.glowie_app.scripts.domain.ScriptId
import glowie.glowie_app.scripts.domain.ScriptRepository

class GetScriptContent(
    private val scriptRepository: ScriptRepository,
) {
    suspend operator fun invoke(scriptId: ScriptId): String {
        return scriptRepository.getScriptContent(scriptId)
    }
}
