package com.caseyjbrooks.netlify

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.asPromise
import kotlinx.coroutines.async
import kotlin.js.Promise

class App(routes: Router.()->Unit) {

    val router = RootRouter(routes)
    val env = EnvVar()

    @JsName("call")
    fun call(method: String, path: String, body: dynamic): Promise<*> {
        return GlobalScope.async { router.call(method, path, body) }.asPromise()
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
