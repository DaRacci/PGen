package dev.racci.pgen

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.contentLength
import io.ktor.utils.io.bits.lowInt
import kotlinx.cinterop.toKString
//
//public object FileService {
//    internal actual suspend fun installNeededFiles() {
//        val client = HttpClient()
//        val response = client.get(WORD_URL)
//        val contentLength = response.contentLength()?.lowInt ?: 0
//        val byteArray = ByteArray(contentLength)
//        var offset = 0
//        do {
//            val currentRead = response.bodyAsChannel().readAvailable(byteArray, offset, byteArray.size)
//            offset += currentRead
//            Logger.debug { "Download in progress, offset: $offset, current read $currentRead / $contentLength" }
//        } while (offset < contentLength)
//        Logger.debug { "Download done" }
//
//        Logger.debug { byteArray.toKString() }
//    }
//}
