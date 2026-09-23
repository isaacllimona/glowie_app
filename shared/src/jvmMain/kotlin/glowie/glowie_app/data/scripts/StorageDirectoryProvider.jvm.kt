package glowie.glowie_app.data.scripts

import kotlinx.io.files.Path

actual fun getStorageDirectory(): Path {
    return Path(System.getProperty("user.home"), ".glowie")
}