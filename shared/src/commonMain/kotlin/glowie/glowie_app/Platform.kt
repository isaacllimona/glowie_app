package glowie.glowie_app

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform