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
        console.log("========================= CALLING ROUTE =========================")
        console.log("===== path: [$path]")
        console.log("====================== REQUEST BODY START =======================")
        console.log(JSON.stringify(body, null, 2))
        console.log("======================= REQUEST BODY END ========================")
        console.log("========================= HANDLE BEGIN ==========================")
        return GlobalScope.async {
            val responseCall = router.call(method, path, body)

            console.log("========================== HANDLE END ===========================")
            console.log("====================== RESPONSE BODY START ======================")
            console.log(JSON.stringify(responseCall.body, null, 2))
            console.log("======================= RESPONSE BODY END =======================")
            console.log("=========================== ROUTE END ===========================")
            console.log()
            console.log()

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
