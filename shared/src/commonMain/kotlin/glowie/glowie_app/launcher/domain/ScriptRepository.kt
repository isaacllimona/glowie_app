package glowie.glowie_app.launcher.domain

interface ScriptRepository {
    suspend fun listScripts(): List<Script>
    suspend fun getScriptContent(id: String): String
}