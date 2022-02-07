package dev.racci.pgen

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.contentLength
import io.ktor.utils.io.bits.lowInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.File
import java.nio.file.Path
import kotlin.system.exitProcess

public actual object FileService {

    private val parentFolder by lazy {
        FileService::class.java.protectionDomain.codeSource.location.path.substringBeforeLast("/")
    }

    public actual val wordMap: MutableMap<Int, Set<String>> by lazy {
        val inputStream = FileService::class.java.classLoader.getResourceAsStream("words.json")
        inputStream!!.use {
            Json.decodeFromStream(it)
        }
    }

    public actual suspend fun getRulePreset(filePath: String?): Rules? {
        val file = filePath?.toFile() ?: Path.of(parentFolder, "rules.json").toFile()

        try {
            if (filePath == null && !file.exists()) { createDefaultFile(true) }
            require(file.exists()) { "${file.path} doesn't exist." }
            require(file.isFile) { "${file.path} is not a valid file." }
        } catch (e: IllegalArgumentException) {
            Logger.error { "Error with file: ${e.message}" }
            exitProcess(1)
        }

        return file.inputStream().use {
            val string = it.readBytes().decodeToString()
            try {
                json.decodeFromString(string)
            } catch (ex: Exception) {
                Logger.error { "Error with decoding file: ${ex.message}" }
                exitProcess(1)
            }
        }
    }

    internal actual suspend fun createDefaultFile(create: Boolean) {
        if (!create) return
        Logger.debug { "Creating default file." }
        val file = Path.of(parentFolder, "rules.json").toFile()
        if (file.exists()) {
            Logger.error { "rules.json file already exists." }
            exitProcess(1)
        }
        withContext(Dispatchers.IO) {
            try {
                file.createNewFile()
                file.outputStream().use { stream ->
                    json.encodeToStream(Rules.serializer(), Rules(), stream)
                }
            } catch (ex: Exception) {
                Logger.error { "Error while creating default file: ${ex.message}" }
                exitProcess(1)
            }
        }
    }

    internal actual suspend fun installNeededFiles() {
        val client = HttpClient()
        val response = client.get(WORD_URL)

        val contentLength = response.contentLength()?.lowInt ?: 0
        val byteArray = ByteArray(contentLength)
        var offset = 0
        do {
            val currentRead = response.bodyAsChannel().readAvailable(byteArray, offset, byteArray.size)
            offset += currentRead
            Logger.debug { "Download in progress, offset: $offset, current read $currentRead / $contentLength" }
        } while (offset < contentLength)
        Logger.debug { "Download done" }

        Logger.debug { byteArray.decodeToString() }
    }

    private fun String?.toFile(): File? =
        if (this == null) null else File(this)
}
