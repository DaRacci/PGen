package dev.racci.pgen

import com.soywiz.korio.async.use
import com.soywiz.korio.file.std.localVfs
import com.soywiz.korio.stream.readAll
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.contentLength
import io.ktor.utils.io.bits.lowInt
import kotlinx.cinterop.toKString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.system.exitProcess

public actual object FileService {

    public actual val wordMap: MutableMap<Int, Set<String>> by lazy { TODO() }

    public actual suspend fun getRulePreset(
        filePath: String?,
        override: Boolean
    ): Rules? {
        filePath ?: return null
        val file = localVfs(filePath)

        try {
            require(file.exists()) { "${file.path} doesn't exist." }
            require(file.isFile()) { "${file.path} is not a valid file." }
        } catch (e: IllegalArgumentException) {
            Logger.error { "Error with file: ${e.message}" }
            exitProcess(1)
        }

        return file.openInputStream().use {
            val string = readAll().toKString()
            Json.decodeFromString(string)
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

        Logger.debug { byteArray.toKString() }
    }

    internal actual suspend fun createDefaultFile(
        create: Boolean,
        override: Boolean?,
        exitAfter: Boolean
    ) { TODO() }
}
