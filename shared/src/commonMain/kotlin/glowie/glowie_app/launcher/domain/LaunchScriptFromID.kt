package glowie.glowie_app.launcher.domain

class LaunchScriptFromID(
    private val scriptRepository: ScriptRepository,
    private val launchScript: LaunchScript
) {
    suspend operator fun invoke(scriptId: String) {
        val script = scriptRepository.getScriptContent(scriptId)
        launchScript(script)
    }

    fun stop() {
        launchScript.stop()
    }
}
