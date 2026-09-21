package glowie.glowie_app.scripts.domain

interface ScriptRepository {
    suspend fun listScripts(): List<ScriptHeader>
    suspend fun getScriptContent(scriptId: ScriptId): String
    suspend fun saveScript(id: ScriptId, metadata: ScriptMetadata, content: String)
}
