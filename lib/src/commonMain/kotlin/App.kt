package com.caseyjbrooks.netlify

import com.caseyjbrooks.netlify.router.http.HttpRootRouter
import com.caseyjbrooks.netlify.router.http.HttpRouter
import com.caseyjbrooks.netlify.router.http.Request
import com.caseyjbrooks.netlify.router.http.Response

class App(routes: HttpRouter.() -> Unit) {

    val router = HttpRootRouter(routes)
    val env = EnvVar()

    suspend fun call(method: String, path: String, body: String): Response {
        return router.call(Request(this, method.toUpperCase(), "/${path.trim('/')}", body))
    }
}
