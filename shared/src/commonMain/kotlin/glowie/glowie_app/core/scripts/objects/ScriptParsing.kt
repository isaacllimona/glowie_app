package glowie.glowie_app.core.scripts.objects

internal data class ScriptAnnotations (
    val name: String,
    val description: String
)

internal fun parseScriptAnnotations(content: String) : ScriptAnnotations {
    val nameRegex = """-- *@name (.+)""".toRegex()
    val name = nameRegex.find(content)?.groups?.get(1)?.value ?: ""

    val descRegex = """-- *@desc (.+)""".toRegex()
    val description = descRegex.find(content)?.groups?.get(1)?.value ?: ""

    return ScriptAnnotations(name=name, description=description)
}
