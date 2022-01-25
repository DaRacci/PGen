package dev.racci.pgen

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File
import kotlin.system.exitProcess

public actual object FileService {

    public actual val wordMap: MutableMap<Int, Set<String>> by lazy {
        val inputStream = FileService::class.java.classLoader.getResourceAsStream("words.json")
        inputStream!!.use {
            Json.decodeFromStream(it)
        }
    }

    public actual suspend fun getRulePreset(filePath: String?): Rules? {
        filePath ?: return null
        val file = File(filePath)

        try {
            require(file.exists()) { "${file.path} doesn't exist." }
            require(file.isFile) { "${file.path} is not a valid file." }
        } catch (e: IllegalArgumentException) {
            Logger.error { "Error with file: ${e.message}" }
            exitProcess(1)
        }

        return file.inputStream().use {
            val string = it.readBytes().decodeToString()
            Json.decodeFromString(string)
        }
    }
}
