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
)

@Serializable
data class ScriptHeader(
    val id: ScriptId,
    val metadata: ScriptMetadata
)
