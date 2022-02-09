package dev.racci.pgen

import kotlin.system.exitProcess

public actual fun afterGen() {
    exitProcess(0)
//    val tk = Toolkit.getDefaultToolkit()
//    val cb = tk.systemClipboard
//    val strSel = StringSelection(password)
//    cb.setContents(strSel, null)
//    exitProcess(0)
}
