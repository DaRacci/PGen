package dev.racci.pgen

import com.soywiz.korio.async.use
import com.soywiz.korio.file.std.localVfs
import com.soywiz.korio.stream.readAll
import io.ktor.client.HttpClient
import io.ktor.client.request.request
import io.ktor.client.statement.HttpStatement
import io.ktor.http.contentLength
import io.ktor.utils.io.bits.lowInt
import io.ktor.utils.io.core.ExperimentalIoApi
import kotlin.system.exitProcess
import kotlinx.cinterop.toKString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@ExperimentalIoApi
public actual object FileService {

    public actual val wordMap: MutableMap<Int, Set<String>> by lazy {
        // TODO
        mutableMapOf()
    }

    public actual suspend fun getRulePreset(filePath: String?): Rules? {
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
        val response = client.request<HttpStatement>("https://gist.githubusercontent.com/DaRacci/ba19d5f7d490b63a39286eaa4f7dbf99/raw/daf738bcb94faebbf1ede1e408f89d58c30db240/words.json")
        val byteArrayB: ByteArray =
            response.execute {
                val contentLength = it.contentLength()?.lowInt ?: 0
                val byteArray = ByteArray(contentLength)
                var offset = 0
                do {
                    val currentRead = it.content.readAvailable(byteArray, offset, byteArray.size)
                    offset += currentRead
                    Logger.debug { "Download in progress, offset: $offset, current read $currentRead / $contentLength" }
                } while (offset < contentLength)
                Logger.debug { "Download done" }
                byteArray
            }
        Logger.debug { byteArrayB.toKString() }
    }
}
