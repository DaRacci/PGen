package dev.racci.pgen

import kotlin.native.concurrent.ThreadLocal

internal const val ESCAPE = '\u001B'
internal const val RESET = "$ESCAPE[0m"

@ThreadLocal
public object Logger {

    public var debug: Boolean = false

    public fun info(lazy: () -> String) {
        println("$ESCAPE[36m[INFO]$RESET -> ${lazy()}")
    }

    public fun warn(lazy: () -> String) {
        println("$ESCAPE[33m[WARN]$RESET -> ${lazy()}")
    }

    public fun error(lazy: () -> String) {
        println("$ESCAPE[31m[ERROR]$RESET -> ${lazy()}")
    }

    public fun debug(lazy: () -> String) {
        if (debug) println("$ESCAPE[90m[DEBUG]$RESET -> ${lazy()}")
    }
}
