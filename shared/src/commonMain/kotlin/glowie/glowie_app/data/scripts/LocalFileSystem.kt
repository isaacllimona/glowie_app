package glowie.glowie_app.data.scripts

import kotlinx.io.files.Path
import kotlinx.io.files.FileSystem
import kotlinx.io.buffered
import kotlinx.io.readString

import kotlinx.io.files.SystemFileSystem
import kotlinx.io.writeString

class LocalFileSystem(
    baseDir: String,
    storagePath: Path = getStorageDirectory(),
    private val fileSystem: FileSystem = SystemFileSystem,
) {
    private val filesPath = Path(storagePath, baseDir)
    private fun makePath(filename: String) = Path(filesPath, filename)

    init {
        fileSystem.createDirectories(filesPath)
        if (!(fileSystem.metadataOrNull(filesPath)?.isDirectory ?: false)) {
            throw Exception("Failed to create directory $filesPath")
        }
    }

    fun exists(filename: String) = fileSystem.exists(makePath(filename))

    fun read(filename: String): String? {
        val filePath = makePath(filename)
        if (!fileSystem.exists(filePath)) { return null }
        val content = fileSystem.source(filePath).buffered()
            .use { source -> source.readString() }
        return content
    }

    fun write(filename: String, content: String) {
        val filePath = makePath(filename)
        fileSystem.sink(filePath).buffered()
            .use { sink -> sink.writeString(content) }
    }

    fun delete(filename: String) {
        val filePath = makePath(filename)
        fileSystem.delete(filePath, mustExist=false)
    }
}
