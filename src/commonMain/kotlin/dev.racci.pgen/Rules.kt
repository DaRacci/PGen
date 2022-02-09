package dev.racci.pgen

import kotlinx.serialization.Serializable

@Serializable
public data class Rules(
    val words: Int = 2,
    val minLength: Int = 5,
    val maxLength: Int = 7,
    val transform: String = "CAPITALISE",
    val separatorChar: String = "RANDOM",
    val separatorAlphabet: String = "!@$%.&*-+=?:;",
    val matchRandomChar: Boolean = true,
    val digitsBefore: Int = 0,
    val digitsAfter: Int = 3,
    val amount: Int = 3,
)
