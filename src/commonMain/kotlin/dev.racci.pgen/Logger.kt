package dev.racci.pgen

import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.terminal.Terminal
import kotlin.native.concurrent.ThreadLocal

private val terminal by lazy { Terminal() }

@ThreadLocal
public object Logger {

    public var debug: Boolean = false

    public fun info(lazy: () -> String) {
        terminal.println(TextColors.green("[INFO]") + " -> ${lazy()}")
    }

    public fun warn(lazy: () -> String) {
        terminal.println(TextColors.yellow("[WARN]") + " -> ${lazy()}")
    }

    public fun error(lazy: () -> String) {
        terminal.println(TextColors.red("[ERROR]") + " -> ${lazy()}")
    }

    public fun debug(lazy: () -> String) {
        if (debug) terminal.println(TextColors.magenta("[DEBUG]") + " -> ${lazy()}")
    }
}
