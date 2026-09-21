package glowie.glowie_app.scripts.domain

data class SavePayload (
    val id: ScriptId,
    val metadata: ScriptMetadata,
    val content: String,
)

sealed class SaveStatus {
    data class Invalid(val msg: String) : SaveStatus()
    data class Ready(val payload: SavePayload) : SaveStatus()
}

class SaveScript (
    private val repository: ScriptRepository
) {
    fun prepareSave(content: String, origin: ScriptOrigin): SaveStatus {
        val annotations = parseScriptAnnotations(content)

        if (annotations.name.isEmpty()) return SaveStatus.Invalid("Script has no name")

        val metadata = ScriptMetadata(
            name = annotations.name,
            description = annotations.description,
            origin = origin
        )
        val id = createScriptId(metadata)
        val savePayload = SavePayload(id = id, metadata = metadata, content = content)
        return SaveStatus.Ready(savePayload)
    }

    suspend fun save(saveStatus: SaveStatus.Ready) {
        repository.saveScript(
            id = saveStatus.payload.id,
            metadata = saveStatus.payload.metadata,
            content = saveStatus.payload.content
        )
    }
}

fun createScriptId(metadata: ScriptMetadata) = ScriptId("${metadata.name}@${metadata.origin}")
