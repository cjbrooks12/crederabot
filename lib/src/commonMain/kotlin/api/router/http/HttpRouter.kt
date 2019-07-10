package com.caseyjbrooks.netlify.api.router.http

open class HttpRouter : ApiHandler {

    internal val handlers: MutableList<ApiHandler> = mutableListOf()
    internal val beforeMiddlewares: MutableList<BeforeMiddleware> = mutableListOf()
    internal val afterMiddlewares: MutableList<AfterMiddleware> = mutableListOf()

    override suspend fun matches(request: Request): Boolean {
        val actualRequest = transformRequest(request).first
        return handlers.any { it.matches(actualRequest) }
    }

    override suspend fun call(request: Request): Response? {
        val requestPair = transformRequest(request)
        val apiHandler = handlers.firstOrNull { it.matches(requestPair.first) }
        if (apiHandler != null) {
            return transformResponse(requestPair.first, requestPair.second, apiHandler)
        } else {
            return null
        }
    }

    override fun getPaths(): List<Pair<String, String>> {
        return handlers.flatMap { it.getPaths() }
    }

    private suspend fun transformRequest(request: Request): Pair<Request, Response?> {
        var actualRequest: Request? = request
        var actualResponse: Response? = null

        for (middleware in beforeMiddlewares) {
            val middlewareResult = middleware.call(actualRequest!!)
            if (middlewareResult.first != null) {
                actualRequest = middlewareResult.first
            } else if (middlewareResult.second != null) {
                actualResponse = middlewareResult.second
                break
            }
        }
        return Pair(actualRequest!!, actualResponse)
    }

    private suspend fun transformResponse(request: Request, response: Response?, handler: ApiHandler): Response {
        var actualResponse: Response?
        if (response != null) {
            actualResponse = response
        } else {
            actualResponse = handler.call(request)
        }

        if (actualResponse != null) {
            for (middleware in afterMiddlewares) {
                actualResponse = middleware.call(request, actualResponse!!)
            }
        }

        return actualResponse!!
    }
}
