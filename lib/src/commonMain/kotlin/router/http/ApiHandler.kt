package com.caseyjbrooks.netlify.router.http

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
     * List the HTTP paths registered under this handler. Returns pairs of
     */
    fun getPaths(): List<Pair<String, String>>

}