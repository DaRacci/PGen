
import com.soywiz.korio.async.async
import com.soywiz.korio.async.use
import com.soywiz.korio.file.std.localVfs
import com.soywiz.korio.stream.readAll
import io.ktor.client.HttpClient
import io.ktor.client.engine.curl.Curl
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toKString
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import platform.posix.fclose
import platform.posix.fgets
import platform.posix.fopen

fun main(args: Array<String>) {
    runBlocking {
        val wordList = async {
            val file = localVfs("words.txt")
            if (!file.exists()) {
                // Download the word list from https://raw.githubusercontent.com/dwyl/english-words/master/words_alpha.txt
                val client = HttpClient(Curl.create())
            }
        }
        val parser = ArgParser("test")

        val preset by parser.option(ArgType.String, "file", "f", "Point to a file to use for the options.")
        val words by parser.option(ArgType.Int, "words", "w", "Amount of full words.")
        val minLength by parser.option(ArgType.Int, "minLength", "ml", "Minimum length of words.")
        val maxLength by parser.option(ArgType.Int, "maxLength", "Ml", "Maximum length of words.")
        val transform by parser.option(ArgType.String, "transform", "t", "What transform mode to use.")
        val separatorChar by parser.option(ArgType.String, "separatorChar", "sc", "")
        val separatorAlphabet by parser.option(ArgType.String, "separatorAlphabet", "sa", "")
        val digitsBefore by parser.option(ArgType.Int, "digitsBefore", "db", "")
        val digitsAfter by parser.option(ArgType.Int, "digitsAfter", "da", "")

        parser.parse(args)

        var filePreset: Rules? = null

        if (preset != null) {
            val file = localVfs(preset!!)
            require(file.exists()) { "${file.path} doesn't exist." }
            require(file.isFile()) { "${file.path} is not a valid file." }
            val inputStream = file.openInputStream()
            inputStream.use {
                val string = readAll().toKString()
                filePreset = Json.decodeFromString<Rules>(string)
            }
        }
        val finalRules =
            Rules(
                words = words ?: filePreset?.words ?: 2,
                minLength = minLength ?: filePreset?.minLength ?: 5,
                maxLength = maxLength ?: filePreset?.maxLength ?: 7,
                transform = transform ?: filePreset?.transform ?: "CAPITALISE",
                separatorChar = separatorChar ?: filePreset?.separatorChar ?: "-",
                separatorAlphabet = separatorAlphabet ?: filePreset?.separatorAlphabet ?: "!@$%.&*-+=?:;",
                digitsBefore = digitsBefore ?: filePreset?.digitsBefore ?: 0,
                digitsAfter = digitsAfter ?: filePreset?.digitsAfter ?: 3,
            )

        println(finalRules)
    }
}

@Serializable
data class Rules(
    val words: Int? = null,
    val minLength: Int? = null,
    val maxLength: Int? = null,
    val transform: String? = null,
    val separatorChar: String? = null,
    val separatorAlphabet: String? = null,
    val digitsBefore: Int? = null,
    val digitsAfter: Int? = null,
)

fun readAllText(filePath: String): String {
    val returnBuffer = StringBuilder()
    val file = fopen(filePath, "READ")
        ?: throw IllegalArgumentException("Cannot open input file $filePath")

    try {
        memScoped {
            val readBufferLength = 64 * 1024
            val buffer = allocArray<ByteVar>(readBufferLength)
            var line = fgets(buffer, readBufferLength, file)?.toKString()
            while (line != null) {
                returnBuffer.append(line)
                line = fgets(buffer, readBufferLength, file)?.toKString()
            }
        }
    } finally {
        fclose(file)
    }

    return returnBuffer.toString()
}

enum class TransformMode {
    NONE,
    CAPITALISE,
    CAPITALISE_ALL_BUT_FIRST_LETTER,
    UPPERCASE,
    LOWERCASE,
    RANDOM,
    ALTERNATING,
}
