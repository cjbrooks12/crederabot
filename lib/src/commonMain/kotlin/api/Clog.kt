package com.caseyjbrooks.netlify.api

object Clog {

    private const val ESC = "\u001B"
    private const val RESET = "$ESC[0m"

    private const val FG_RED = "$ESC[31m"
    private const val FG_GREEN = "$ESC[32m"
    private const val FG_YELLOW = "$ESC[33m"
    private const val FG_BLUE = "$ESC[34m"
    private const val FG_MAGENTA = "$ESC[35m"
    private const val FG_CYAN = "$ESC[36m"

    private fun red(message: String) = "$FG_RED$message$RESET"
    private fun green(message: String) = "$FG_GREEN$message$RESET"
    private fun yellow(message: String) = "$FG_YELLOW$message$RESET"
    private fun blue(message: String) = "$FG_BLUE$message$RESET"
    private fun magenta(message: String) = "$FG_MAGENTA$message$RESET"
    private fun cyan(message: String) = "$FG_CYAN$message$RESET"

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

