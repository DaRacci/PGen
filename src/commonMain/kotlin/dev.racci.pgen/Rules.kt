package dev.racci.pgen

import kotlinx.serialization.Serializable

@Serializable
public data class Rules(
    val words: Int? = null,
    val minLength: Int? = null,
    val maxLength: Int? = null,
    val transform: String? = null,
    val separatorChar: String? = null,
    val separatorAlphabet: String? = null,
    val digitsBefore: Int? = null,
    val digitsAfter: Int? = null,
)
