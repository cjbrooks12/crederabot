package com.caseyjbrooks.netlify

import com.caseyjbrooks.netlify.api.Clog
import com.caseyjbrooks.netlify.impl.DebugEnv
import com.caseyjbrooks.netlify.impl.apps.PlusPlusApp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

fun main() {
    val app = PlusPlusApp(DebugEnv())
    GlobalScope.launch {
        val reply = app.sendTestMessage("@casey++")

        Clog.v("reply=$reply")
    }
}