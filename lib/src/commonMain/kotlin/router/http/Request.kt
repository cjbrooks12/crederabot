package com.caseyjbrooks.netlify.router.http

import com.caseyjbrooks.netlify.App

data class Request(
    val app: App,
    val method: String,
    val path: String,
    val body: Any?
)