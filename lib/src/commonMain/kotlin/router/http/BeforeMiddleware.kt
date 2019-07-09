package com.caseyjbrooks.netlify.router.http

interface BeforeMiddleware {

    /**
     * Transforms the request before handling, or short-circuit the pipeline, given the original request
     */
    suspend fun call(request: Request): Pair<Request?, Response?>
}

fun HttpRouter.before(callback: suspend (Request) -> Pair<Request?, Response?>) {
    beforeMiddlewares.add(object : BeforeMiddleware {
        override suspend fun call(request: Request) = callback(request)
    })
}