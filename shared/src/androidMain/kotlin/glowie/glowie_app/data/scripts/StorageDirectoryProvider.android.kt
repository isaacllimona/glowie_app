package glowie.glowie_app.data.scripts

import glowie.glowie_app.AppContextProvider
import kotlinx.io.files.Path

actual fun getStorageDirectory(): Path {
    val appContext = AppContextProvider.get()
    val storagePath = Path(appContext.filesDir.absolutePath)
    return storagePath
}
