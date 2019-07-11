package com.caseyjbrooks.netlify.api.router.http

interface ApiHandler {

    /**
     * Check if any of this handler or any of its children can handle the request
     */
    suspend fun matches(request: Request): Boolean

    /**
     * Given that this handler or one of its children can handle the request, handle it now
     */
    suspend fun call(request: Request): Response?

    /**
     * List the HTTP paths registered under this handler. Returns pairs of HTTP method verb to path
     */
    fun getPaths(): List<Pair<String, String>>

    /**
     * Implements a Visitor pattern to explore all registered ApiHandlers
     */
    fun visit(onVisit: (ApiHandler) -> Unit)

}