package dev.racci.pgen

import com.soywiz.korio.async.use
import com.soywiz.korio.file.std.localVfs
import com.soywiz.korio.stream.readAll
import kotlinx.cinterop.toKString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.system.exitProcess

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
}
