package com.caseyjbrooks.netlify.api.router.http

import com.caseyjbrooks.netlify.api.App

data class Request(
    val app: App,
    val method: String,
    val path: String,
    val body: Any?
)