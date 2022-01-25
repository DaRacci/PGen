package dev.racci.pgen

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.coroutines.runBlocking
import kotlin.random.Random
import kotlin.random.nextInt

public fun main(args: Array<String>) {
    runBlocking {
        val parser = ArgParser("PGen")

        val preset by parser.option(ArgType.String, "file", "f", "Point to a file to use for the options.")
        val words by parser.option(ArgType.Int, "words", "w", "Amount of full words.")
        val minLength by parser.option(ArgType.Int, "minLength", "ml", "Minimum length of words.")
        val maxLength by parser.option(ArgType.Int, "maxLength", "Ml", "Maximum length of words.")
        val transform by parser.option(ArgType.String, "transform", "t", "What transform mode to use, Options are [NONE, CAPITALISE, CAPITALISE_ALL_BUT_FIRST_LETTER, UPPERCASE, LOWERCASE, RANDOM]")
        val separatorChar by parser.option(ArgType.String, "separatorChar", "sc", "Leave blank or 'none' for no split, 'random' to use randomised characters or use any other UTF-8 compliant character for between words.")
        val separatorAlphabet by parser.option(ArgType.String, "separatorAlphabet", "sa", "Defines the random alphabet used between words.")
        val digitsBefore by parser.option(ArgType.Int, "digitsBefore", "db", "Sets how may digits should be before the password.")
        val digitsAfter by parser.option(ArgType.Int, "digitsAfter", "da", "Sets how many digits should be after the password.")
        val debug by parser.option(ArgType.Boolean, "debug", "d", "Enables debug mode.")

        parser.parse(args)

        debug?.let { Logger.debug = it }

        Logger.debug { "Debugging mode Enabled" }

        val filePreset: Rules? = FileService.getRulePreset(preset)

        val finalRules =
            Rules(
                words = words ?: filePreset?.words ?: 2,
                minLength = minLength ?: filePreset?.minLength ?: 5,
                maxLength = maxLength ?: filePreset?.maxLength ?: 7,
                transform = transform ?: filePreset?.transform ?: "CAPITALISE",
                separatorChar = separatorChar ?: filePreset?.separatorChar ?: "RANDOM",
                separatorAlphabet = separatorAlphabet ?: filePreset?.separatorAlphabet ?: "!@$%.&*-+=?:;",
                digitsBefore = digitsBefore ?: filePreset?.digitsBefore ?: 0,
                digitsAfter = digitsAfter ?: filePreset?.digitsAfter ?: 3,
            )

        Logger.debug { "Your final rule set is $finalRules" }

        generate(finalRules)
    }
}

public fun generate(rules: Rules) {
    val map = FileService.wordMap
    val words = rules.words ?: 2
    val minLength = rules.minLength ?: 2
    val maxLength = rules.maxLength ?: 8
    val transform = rules.transform ?: "CAPITALISE"
    val separatorChar = rules.separatorChar ?: "RANDOM"
    val separatorAlphabet = rules.separatorAlphabet ?: "!@$%.&*-+="
    val digitsBefore = rules.digitsBefore ?: 0
    val digitsAfter = rules.digitsAfter ?: 3

    var finalPassword = ""

    if (digitsBefore > 0) {
        for (i in 1..digitsBefore) {
            finalPassword += Random.nextInt(0, 9)
        }
    }

    Logger.debug { "Added Digits: $finalPassword" }

    mutableListOf<String>()

    var genWords = mutableListOf<String>()
    val rm = Random(Random.nextLong())
    for (num in 1..words) {
        val r = rm.nextInt(minLength..maxLength)
        Logger.debug { "Random number: $r" }
        genWords += map[r]!!.random()
    }
    Logger.debug { "Selected words: $genWords" }

    genWords = transformer(genWords, transform)

    Logger.debug { "Transformed words: $genWords" }

    when (separatorChar) {
        "NONE" -> genWords.forEach { finalPassword += it }
        "RANDOM" -> genWords.forEachIndexed { i, w -> finalPassword += if (i == 0) w else "${separatorAlphabet.random()}$w" }
        else -> genWords.forEachIndexed { i, w -> finalPassword += if (i == 0) w else "${separatorChar.first()}$w" }
    }

    Logger.debug { "After adding words and separator chars: $finalPassword" }

    if (digitsAfter > 0) {
        for (i in 1..digitsAfter) {
            finalPassword += Random.nextInt(0, 9)
        }
    }

    Logger.info { "Final password: $finalPassword" }

    afterGen(finalPassword)
}

public expect fun transformer(list: List<String>, mode: String): MutableList<String>

public expect fun afterGen(password: String)
