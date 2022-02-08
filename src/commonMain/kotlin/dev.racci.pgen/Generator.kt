package dev.racci.pgen

import kotlinx.datetime.Clock
import kotlin.native.concurrent.ThreadLocal
import kotlin.random.Random

@ThreadLocal
public object Generator {

    private val seed by lazy { Random(Clock.System.now().toEpochMilliseconds()) }
    private var selectedRandom: Char? = null

    public fun generate(rules: Rules): String {
        var password = ""

        val getSeperator = { getSeparator(rules.separatorChar, rules.separatorAlphabet, rules.matchRandomChar)?.toString() ?: "" }

        rules.digitsBefore.takeUnless { it < 1 }?.let { password += getDigits(it) + getSeperator() }

        val words = getWords(rules.words, rules.minLength, rules.maxLength)
        val transformedWords = transformer(words, rules.transform)
        password += addSeparators(transformedWords, rules.separatorChar, rules.separatorAlphabet, rules.matchRandomChar)

        rules.digitsAfter.takeUnless { it < 1 }?.let { password += getSeperator() + getDigits(it) }
        return password
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
        Logger.debug { "Generated string: $str" }
        return str
    }
}
