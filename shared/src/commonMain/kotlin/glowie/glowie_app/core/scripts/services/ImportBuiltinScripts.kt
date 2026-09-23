package glowie.glowie_app.core.scripts.services

import glowie.glowie_app.core.scripts.objects.ScriptOrigin
import glowie.glowie_app.core.scripts.repositories.ExternalScriptRepository

class ImportBuiltinScripts(
    private val saveScript: SaveScript,
    private val builtinScriptRepository: ExternalScriptRepository
) {
    suspend operator fun invoke() {
        val builtinScripts = builtinScriptRepository.listScripts()
        for (script in builtinScripts) {
            val content = builtinScriptRepository.getScriptContent(script)
            val saveStatus = saveScript.prepareSave(content, ScriptOrigin.Builtin)
            if (saveStatus is SaveStatus.Ready) { saveScript.save(saveStatus) }
        }
    }
}
