package dev.racci.pgen

import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import kotlin.system.exitProcess

public actual fun afterGen(password: String) {
    val tk = Toolkit.getDefaultToolkit()
    val cb = tk.systemClipboard
    val strSel = StringSelection(password)
    cb.setContents(strSel, null)
    exitProcess(0)
}
