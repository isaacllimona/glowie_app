package glowie.glowie_app.core.scripts.repositories

import glowie.glowie_app.core.scripts.objects.ScriptHeader
import glowie.glowie_app.core.scripts.objects.ScriptId
import glowie.glowie_app.core.scripts.objects.ScriptMetadata

interface ScriptRepository {
    suspend fun listScripts(): List<ScriptHeader>
    suspend fun getScriptContent(scriptId: ScriptId): String
    suspend fun saveScript(id: ScriptId, metadata: ScriptMetadata, content: String)
    suspend fun deleteScript(scriptId: ScriptId)
}
