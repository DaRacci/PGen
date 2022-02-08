package dev.racci.pgen

import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import kotlin.random.Random
import kotlin.system.exitProcess

public actual fun transformer(
    list: Array<String>,
    mode: String,
): Array<String> {
    val words: MutableList<String> = list.toMutableList()
    when (mode.uppercase()) {
        "NONE" -> {}
        "CAPITALISE" -> words.replaceAll { it.lowercase().replaceFirstChar { l -> l.titlecase() } }
        "UPPERCASE_ALL_BUT_FIRST_LETTER" -> words.replaceAll { it.uppercase().replaceFirstChar { l -> l.lowercase() } }
        "UPPERCASE" -> words.replaceAll { it.uppercase() }
        "RANDOM" -> words.replaceAll {
            val cList = it.toMutableList()
            cList.replaceAll { c -> if (Random.nextBoolean()) c.uppercaseChar() else c }
            cList.toCharArray().concatToString()
        }
        "ALTERNATING" -> words.replaceAll {
            val cList = it.toMutableList()
            for ((i, c) in cList.withIndex()) {
                if ((i % 2) == 0) {
                    cList[i] = c.uppercaseChar()
                }
            }
            cList.toCharArray().concatToString()
        }
        else -> Logger.error { "Invalid transform mode provided: $mode" }
    }
    return words.toTypedArray()
}

public actual fun afterGen(password: String) {
    val tk = Toolkit.getDefaultToolkit()
    val cb = tk.systemClipboard
    val strSel = StringSelection(password)
    cb.setContents(strSel, null)
    exitProcess(0)
}
