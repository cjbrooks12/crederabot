package com.caseyjbrooks.netlify.router.http

interface AfterMiddleware {

    /**
     * Transforms the response before sending, given the original request and the original response
     */
    suspend fun call(request: Request, response: Response): Response
}

fun HttpRouter.after(callback: suspend (Request, Response) -> Response) {
    afterMiddlewares.add(object : AfterMiddleware {
        override suspend fun call(request: Request, response: Response) = callback(request, response)
    })
}
