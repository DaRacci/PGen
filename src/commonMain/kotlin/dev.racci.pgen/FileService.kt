package dev.racci.pgen

public expect object FileService {

    public val wordMap: MutableMap<Int, Set<String>>

    public suspend fun getRulePreset(filePath: String?): Rules?

    internal suspend fun installNeededFiles()
}
