package dev.racci.pgen

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand
import kotlinx.cli.default
import kotlinx.cli.optional
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalCli::class)
public fun main(args: Array<String>) {
    runBlocking {
        val parser = ArgParser("PGen")

        var presetRules = Rules()
        class File : Subcommand("file", "File settings") {

            val createFile = CreateFile()
            val readFile = ReadFile()

            init {
                subcommands(createFile, readFile)
            }

            inner class CreateFile : Subcommand("create", "Create a new default file in the folder where the jar file is located.") {

                var execute = false
                val override by option(ArgType.Boolean, "override", "o", "Overrides the existing file with the new one.").default(false)

                override fun execute() {
                    runBlocking {
                        execute = true
                    }
                }
            }

            inner class ReadFile : Subcommand("read", "Read rules for PGen from a json file.") {

                var execute = false
                val file by argument(ArgType.String, "file", "The file to read the rules from.").optional()

                override fun execute() {
                    runBlocking {
                        execute = true
                    }
                }
            }

            override fun execute() {}
        }

        val words by parser.option(ArgType.Int, "words", "w", "Amount of full words.")
        val minLength by parser.option(ArgType.Int, "minLength", "ml", "Minimum length of words.")
        val maxLength by parser.option(ArgType.Int, "maxLength", "Ml", "Maximum length of words.")
        val transform by parser.option(ArgType.String, "transform", "t", "What transform mode to use, Options are [NONE, CAPITALISE, CAPITALISE_ALL_BUT_FIRST_LETTER, UPPERCASE, LOWERCASE, RANDOM]")
        val separatorChar by parser.option(ArgType.String, "separatorChar", "sc", "Leave blank or 'none' for no split, 'random' to use randomised characters or use any other UTF-8 compliant character for between words.")
        val matchRandomChar by parser.option(ArgType.Boolean, "matchRandomChar", "mrc", "Instead of everyone separator being random they will all use the same one random char.")
        val separatorAlphabet by parser.option(ArgType.String, "separatorAlphabet", "sa", "Defines the random alphabet used between words.")
        val digitsBefore by parser.option(ArgType.Int, "digitsBefore", "db", "Sets how may digits should be before the password.")
        val digitsAfter by parser.option(ArgType.Int, "digitsAfter", "da", "Sets how many digits should be after the password.")
        val amount by parser.option(ArgType.Int, "amount", "a", "Sets how many passwords to generate.")
        val debug by parser.option(ArgType.Boolean, "debug", "d", "Enables debug mode.")

        val file = File()
        parser.subcommands(file)

        parser.parse(args)

        debug?.let { Logger.debug = it }

        Logger.debug { "Debugging mode Enabled" }

        file.createFile.execute.ifTrue { FileService.createDefaultFile(file.createFile.execute, file.createFile.override, true) }
        file.readFile.execute.ifTrue { FileService.getRulePreset(file.readFile.file, false)?.let { rules -> presetRules = rules } }

        val finalRules =
            Rules(
                words = words ?: presetRules.words,
                minLength = minLength ?: presetRules.minLength,
                maxLength = maxLength ?: presetRules.maxLength,
                transform = transform ?: presetRules.transform,
                separatorChar = separatorChar ?: presetRules.separatorChar,
                separatorAlphabet = separatorAlphabet ?: presetRules.separatorAlphabet,
                matchRandomChar = matchRandomChar ?: presetRules.matchRandomChar,
                digitsBefore = digitsBefore ?: presetRules.digitsBefore,
                digitsAfter = digitsAfter ?: presetRules.digitsAfter,
                amount = amount ?: presetRules.amount
            )

        Logger.debug { "Your final rule set is $finalRules" }

        val passwords = Generator.generate(finalRules)

        Logger.info { "Generated password${if (passwords.size > 1) "s" else ""}:" }
        for (password in passwords) {
            Logger.info { password }
        }

        afterGen()
    }
}

private suspend infix fun Boolean?.ifTrue(block: suspend () -> Unit) {
    if (this == true) block()
}

public expect fun afterGen()
