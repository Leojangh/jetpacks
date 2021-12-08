package com.genlz

import javax.script.ScriptEngineManager

fun a(): Any? {
    val jsEngine = ScriptEngineManager().getEngineByName("js")
    return jsEngine.eval("3+3")
}