package glowie.glowie_app.data.scripts

import glowie.glowie_app.core.scripts.repositories.ExternalScriptId
import glowie.glowie_app.core.scripts.repositories.ExternalScriptRepository
import glowie_app.shared.generated.resources.Res

class BuiltinScriptRepository : ExternalScriptRepository {
    override suspend fun listScripts(): List<ExternalScriptId> {
        val manifestBytes = Res.readBytes("files/builtin_scripts/manifest.txt")
        return manifestBytes.decodeToString().trim().lines()
            .filter { it.endsWith(".lua") }
            .map { ExternalScriptId(it) }
    }

    override suspend fun getScriptContent(id: ExternalScriptId): String {
        val filename = id.value
        val bytes = Res.readBytes("files/builtin_scripts/$filename")
        return bytes.decodeToString()
    }
}
