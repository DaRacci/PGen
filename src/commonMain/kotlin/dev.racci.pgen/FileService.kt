package dev.racci.pgen

import kotlinx.serialization.json.Json

internal const val WORD_URL: String = "https://gist.githubusercontent.com/DaRacci/ba19d5f7d490b63a39286eaa4f7dbf99/raw/daf738bcb94faebbf1ede1e408f89d58c30db240/words.json"
internal const val EXCLUDE_URL: String = ""

internal val json by lazy {
    Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
    }
}

public expect object FileService {

    public val wordMap: MutableMap<Int, Set<String>>

    public suspend fun getRulePreset(filePath: String?): Rules?

    internal suspend fun createDefaultFile(create: Boolean)

    internal suspend fun installNeededFiles()
}
