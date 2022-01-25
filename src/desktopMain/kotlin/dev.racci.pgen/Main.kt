package dev.racci.pgen

import com.soywiz.kmem.isEven
import kotlin.random.Random

public actual fun transformer(
    list: List<String>,
    mode: String,
): MutableList<String> {
    val words: MutableList<String> = list.toMutableList()
    when (mode) {
        "NONE" -> {}
        "CAPITALISE" -> words.replaceAll { it.lowercase().replaceFirstChar { l -> l.titlecase() } }
        "CAPITALISE_ALL_BUT_FIRST_LETTER" -> words.replaceAll { it.uppercase().replaceFirstChar { l -> l.lowercase() } }
        "UPPERCASE" -> words.replaceAll { it.uppercase() }
        "LOWERCASE" -> words.replaceAll { it.lowercase() }
        "RANDOM" -> words.replaceAll { it.toCharArray().map { c -> if (Random.nextBoolean()) c.uppercaseChar() else c }.toString() }
        "ALTERNATING" -> words.replaceAll { it.toCharArray().mapIndexed { i, c -> if (i.isEven) c.uppercaseChar() else c }.toString() }
        else -> error { "Invalid transform mode provided: $mode" }
    }
    return words
}

public actual fun afterGen(password: String) {
    // TODO
}
