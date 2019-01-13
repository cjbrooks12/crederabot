package com.caseyjbrooks.netlify

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.asPromise
import kotlinx.coroutines.async
import kotlin.js.Promise

class App(routes: Router.()->Unit) {

    val router = RootRouter(routes)
    val env = EnvVar()

    @JsName("call")
    fun call(method: String, path: String, body: dynamic): Promise<Response> {
        println("========================= CALLING ROUTE =========================")
        println("===== path: [$path]")
        println("====================== REQUEST BODY START =======================")
        println(JSON.stringify(body, null, 2))
        println("======================= REQUEST BODY END ========================")
        println("========================= HANDLE BEGIN ==========================")
        return GlobalScope.async {
            val responseCall = router.call(method, path, body)

            println("========================== HANDLE END ===========================")
            println("====================== RESPONSE BODY START ======================")
            println(JSON.stringify(responseCall.body, null, 2))
            println("======================= RESPONSE BODY END =======================")
            println("=========================== ROUTE END ===========================")
            println()
            println()

            responseCall
        }.asPromise()
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
