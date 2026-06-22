package glowie.glowie_app.launcher.data

import glowie.glowie_app.launcher.domain.Script
import glowie.glowie_app.launcher.domain.ScriptRepository
import glowie_app.shared.generated.resources.Res

class BuiltinScriptRepository : ScriptRepository {
    override suspend fun listScripts(): List<Script> {
        val manifestBytes = Res.readBytes("files/builtin_scripts/manifest.txt")
        return manifestBytes.decodeToString().trim().lines()
            .filter { it.endsWith(".lua") }
            .map { filename ->
                val id = filename.removeSuffix(".lua")
                Script(id = id, name = id, description = "")
            }
    }

    override suspend fun getScriptContent(id: String): String {
        val bytes = Res.readBytes("files/builtin_scripts/$id.lua")
        return bytes.decodeToString()
    }
}
