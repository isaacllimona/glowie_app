package glowie.glowie_app.core.scripts.objects

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class ScriptId(val value: String)

enum class ScriptOrigin { User, Builtin }

@Serializable
data class ScriptMetadata(
    val name: String,
    val description: String,
    val origin: ScriptOrigin,
) {
    fun makeId() : ScriptId? = if (name.isEmpty()) null else ScriptId("$name@$origin")
}

@Serializable
data class ScriptHeader(
    val id: ScriptId,
    val metadata: ScriptMetadata
)

@JvmInline
value class ScriptContent(val value: String) {
    fun generateMetadata(origin: ScriptOrigin): ScriptMetadata {
        val annotations = parseScriptAnnotations(value)
        return ScriptMetadata(
            name = annotations.name,
            description = annotations.description,
            origin = origin
        )
    }
}
