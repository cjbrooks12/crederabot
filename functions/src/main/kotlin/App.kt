package com.caseyjbrooks.netlify

import com.caseyjbrooks.netlify.router.Request
import com.caseyjbrooks.netlify.router.Response
import com.caseyjbrooks.netlify.router.RootRouter
import com.caseyjbrooks.netlify.router.Router
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.asPromise
import kotlinx.coroutines.async
import kotlin.js.Promise

class App(routes: Router.()->Unit) {

    val router = RootRouter(routes)
    val env = EnvVar()

    @JsName("call")
    fun call(method: String, path: String, body: dynamic): Promise<Response> {
        return GlobalScope.async { router.call(Request(method, path, body)) }.asPromise()
    }

    companion object {
        var instance: App? = null
    }

    fun printPaths() = "[" + app().router.getPaths().joinToString { "\"[${it.first}] ${it.second}\"" } + "]"

}

fun app(): App {
    if(App.instance == null) {
        App.instance = App { init() }
    }
    return App.instance!!
}

