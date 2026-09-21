package glowie.glowie_app.scripts.data

import glowie.glowie_app.scripts.domain.ScriptHeader
import glowie.glowie_app.scripts.domain.ScriptId
import glowie.glowie_app.scripts.domain.ScriptMetadata
import glowie.glowie_app.scripts.domain.ScriptRepository
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException

@Serializable
private data class ScriptIndexEntry(
    val metadata: ScriptMetadata,
    val filename: String
)

@Serializable
private data class ScriptIndex(val scripts: Map<ScriptId, ScriptIndexEntry>)

class LocalScriptRepository(
    private val fileSystem: LocalFileSystem = LocalFileSystem(baseDir = "scripts")
) : ScriptRepository {
    private val indexFilename = "index.json"

    private lateinit var index: MutableMap<ScriptId, ScriptIndexEntry>

    init {
        fetchIndex()
    }

    private fun fetchIndex() {
        // Create index if it does not exist
        if (!fileSystem.exists(indexFilename)) {
            index = mutableMapOf()
            saveIndex()
            return
        }

        val rawindex = fileSystem.read(indexFilename) ?: error("Script index does not exist")
        try {
            index = Json.decodeFromString<ScriptIndex>(rawindex).scripts.toMutableMap()
        } catch (_: SerializationException) {
            error ("The script index could not be decoded")
        }
    }

    private fun saveIndex() {
        fileSystem.write(indexFilename, Json.encodeToString<ScriptIndex>(ScriptIndex(index)))
    }

    override suspend fun listScripts(): List<ScriptHeader> {
        return index.map { (id, entry) ->
            ScriptHeader(id = id, metadata = entry.metadata)
        }
    }

    override suspend fun getScriptContent(scriptId: ScriptId): String {
        val script = index[scriptId] ?: error("Script $scriptId does not exist")
        val content = fileSystem.read(script.filename) ?: error("File ${script.filename} does not exist")
        return content
    }

    override suspend fun saveScript(id: ScriptId, metadata: ScriptMetadata, content: String) {
        val filename = "${id.value}.lua"
        val newScriptIndexEntry = ScriptIndexEntry(
            metadata = metadata,
            filename = filename,
        )

        // Save script to file
        fileSystem.write(filename, content)

        // Update index
        index[id] = newScriptIndexEntry
        saveIndex()
    }

    override suspend fun deleteScript(scriptId: ScriptId) {
        val entry = index[scriptId] ?: return

        // Delete script file
        fileSystem.delete(entry.filename)

        // Update index
        index.remove(scriptId)
        saveIndex()
    }
}
