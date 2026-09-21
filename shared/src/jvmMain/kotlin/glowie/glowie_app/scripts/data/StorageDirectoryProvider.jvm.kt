package glowie.glowie_app.scripts.data

import kotlinx.io.files.Path

actual fun getStorageDirectory(): Path {
    return Path(System.getProperty("user.home"), ".glowie")
}