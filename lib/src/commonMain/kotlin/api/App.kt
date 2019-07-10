package com.caseyjbrooks.netlify.api

import com.caseyjbrooks.netlify.api.router.http.HttpRootRouter
import com.caseyjbrooks.netlify.api.router.http.Request
import com.caseyjbrooks.netlify.api.router.http.Response

interface App {

    val env: EnvVar
    val router: HttpRootRouter

    suspend fun call(method: String, path: String, body: String): Response {
        return router.call(Request(this, method.toUpperCase(), "/${path.trim('/')}", body))
    }
}
