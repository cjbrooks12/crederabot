package com.caseyjbrooks.netlify.router

interface BeforeMiddleware {
    suspend fun call(request: Request): Pair<Request?, Response?>
}

interface AfterMiddleware {
    suspend fun call(request: Request, response: Response): Response
}
