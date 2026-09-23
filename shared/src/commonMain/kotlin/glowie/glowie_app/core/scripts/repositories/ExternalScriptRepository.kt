package glowie.glowie_app.core.scripts.repositories

@JvmInline
value class ExternalScriptId(val value: String)

interface ExternalScriptRepository {
    suspend fun listScripts(): List<ExternalScriptId>
    suspend fun getScriptContent(id: ExternalScriptId): String
}