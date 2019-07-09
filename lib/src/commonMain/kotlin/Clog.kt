package com.caseyjbrooks.netlify

import com.caseyjbrooks.netlify.ConsoleControl.blue
import com.caseyjbrooks.netlify.ConsoleControl.cyan
import com.caseyjbrooks.netlify.ConsoleControl.green
import com.caseyjbrooks.netlify.ConsoleControl.magenta
import com.caseyjbrooks.netlify.ConsoleControl.red
import com.caseyjbrooks.netlify.ConsoleControl.yellow

object ConsoleControl {
    const val ESC = "\u001B"
    const val RESET = "$ESC[0m"

    const val FG_RED = "$ESC[31m"
    const val FG_GREEN = "$ESC[32m"
    const val FG_YELLOW = "$ESC[33m"
    const val FG_BLUE = "$ESC[34m"
    const val FG_MAGENTA = "$ESC[35m"
    const val FG_CYAN = "$ESC[36m"

    fun red(message: String) = "$FG_RED$message$RESET"
    fun green(message: String) = "$FG_GREEN$message$RESET"
    fun yellow(message: String) = "$FG_YELLOW$message$RESET"
    fun blue(message: String) = "$FG_BLUE$message$RESET"
    fun magenta(message: String) = "$FG_MAGENTA$message$RESET"
    fun cyan(message: String) = "$FG_CYAN$message$RESET"

}

object Clog {

    fun v(message: String) {
        println("${green("[VERBOSE]")} $message")
    }

    fun d(message: String) {
        println("${blue("[DEBUG]")} $message")
    }

    fun i(message: String) {
        println("${cyan("[INFO]")} $message")
    }

    fun w(message: String) {
        println("${yellow("[WARNING]")} $message")
    }

    fun e(message: String) {
        println("${red("[ERROR]")} $message")
    }

    fun wtf(message: String) {
        println("${magenta("[FATAL]")} $message")
    }
}

