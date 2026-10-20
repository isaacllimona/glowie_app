package glowie.glowie_app.core.scripts.services

import glowie.glowie_app.core.scripts.objects.ScriptOrigin
import glowie.glowie_app.core.scripts.repositories.ExternalScriptRepository
import glowie.glowie_app.core.scripts.repositories.ScriptRepository

class ImportBuiltinScripts(
    private val scriptRepository: ScriptRepository,
    private val builtinScriptRepository: ExternalScriptRepository
) {
    suspend operator fun invoke() {
        val builtinScripts = builtinScriptRepository.listScripts()
        for (script in builtinScripts) {
            val content = builtinScriptRepository.getScriptContent(script)
            val metadata = content.generateMetadata(ScriptOrigin.Builtin)
            val id = metadata.makeId() ?: throw IllegalStateException("Missing metadata for $script")

            scriptRepository.saveScript(id, metadata, content)
        }
    }
}
