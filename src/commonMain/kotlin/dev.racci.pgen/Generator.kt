package dev.racci.pgen

import kotlinx.datetime.Clock
import kotlin.native.concurrent.ThreadLocal
import kotlin.random.Random

@ThreadLocal
public object Generator {

    private val seed by lazy { Random(Clock.System.now().toEpochMilliseconds()) }
    private var selectedRandom: Char? = null

    public fun generate(rules: Rules): Array<String> {
        val passwords = Array(rules.amount) { "" }

        for (i in 0 until rules.amount) {
            val getSeperator = { getSeparator(rules.separatorChar, rules.separatorAlphabet, rules.matchRandomChar)?.toString() ?: "" }

            rules.digitsBefore.takeUnless { it < 1 }?.let { passwords[i] += getDigits(it) + getSeperator() }

            val words = getWords(rules.words, rules.minLength, rules.maxLength)
            val transformedWords = transformWords(words, rules.transform)
            passwords[i] += addSeparators(transformedWords, rules.separatorChar, rules.separatorAlphabet, rules.matchRandomChar)

            rules.digitsAfter.takeUnless { it < 1 }?.let { passwords[i] += getSeperator() + getDigits(it) }
            selectedRandom = null
        }

        return passwords
    }

    private fun getDigits(int: Int): String {
        var digits = ""
        for (i in 0 until int) {
            digits += seed.nextInt(0, 9)
        }
        Logger.debug { "Generated digits: $digits" }
        return digits
    }

    private fun getSeparator(
        separatorChar: String,
        separatorAlphabet: String,
        matchRandomChar: Boolean
    ): Char? {
        val char = when (separatorChar) {
            "NONE" -> null
            "RANDOM" -> if (matchRandomChar) {
                if (selectedRandom == null) {
                    selectedRandom = separatorAlphabet.random(seed)
                }
                selectedRandom
            } else separatorAlphabet.random(seed)
            else -> separatorChar.firstOrNull()
        }
        Logger.debug { "Generated separator: $char" }
        return char
    }

    private fun getWords(
        amount: Int,
        minLength: Int,
        maxLength: Int
    ): Array<String> {
        val words = Array(amount) { "" }
        for (i in 0 until amount) {
            val rInt = seed.nextInt(minLength, maxLength)
            words[i] = FileService.wordMap[rInt]?.random(seed) ?: error("Could not find word of length $rInt, This wasn't meant to happen...")
        }
        return words
    }

    private fun transformWords(
        words: Array<String>,
        mode: String
    ): Array<String> {
        val transformedWords = Array(words.size) { "" }
        when (mode.uppercase()) {
            "NONE" -> {}
            "CAPITALISE" -> words.forEachIndexed { i, w -> transformedWords[i] = w.lowercase().replaceFirstChar { l -> l.titlecase() } }
            "UPPERCASE_ALL_BUT_FIRST_LETTER" -> words.forEachIndexed { i, w -> transformedWords[i] = w.uppercase().replaceFirstChar { l -> l.lowercase() } }
            "UPPERCASE" -> words.forEachIndexed { i, w -> transformedWords[i] = w.uppercase() }
            "RANDOM" -> words.map(String::toCharArray).forEachIndexed { i, w ->
                var word = ""
                w.forEach { c -> word += if (seed.nextBoolean()) c.uppercaseChar() else c }
                transformedWords[i] = word
            }
            "ALTERNATING" -> words.map(String::toCharArray).forEachIndexed { i, w ->
                var word = ""
                for ((i, c) in w.withIndex()) {
                    if ((i % 2) == 0) {
                        word += c.uppercaseChar()
                    }
                }
                transformedWords[i] = word
            }
            else -> Logger.error { "Invalid transform mode provided: $mode" }
        }
        return transformedWords
    }

    private fun addSeparators(
        words: Array<String>,
        separatorChar: String,
        separatorAlphabet: String,
        matchRandomChar: Boolean
    ): String {
        var str = ""
        val iterator = words.iterator()
        while (iterator.hasNext()) {
            var word = iterator.next()
            if (iterator.hasNext()) {
                word += getSeparator(
                    separatorChar,
                    separatorAlphabet,
                    matchRandomChar
                )
            }
            str += word
        }
        Logger.debug { "Generated words: $str" }
        return str
    }
}
