package glowie.glowie_app.cmderrors.domain

data class CmdError(
    val devices: List<String>,
    val message: String,
)
