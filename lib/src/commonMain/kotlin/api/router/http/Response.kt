package com.caseyjbrooks.netlify.api.router.http

data class Response(
    val statusCode: Int,
    val body: Any?
)