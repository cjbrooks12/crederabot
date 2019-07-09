package com.caseyjbrooks.netlify.router.http

data class Response(
    val statusCode: Int,
    val body: Any?
)