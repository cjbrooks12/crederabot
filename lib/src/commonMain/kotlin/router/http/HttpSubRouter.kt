package com.caseyjbrooks.netlify.router.http

class HttpSubRouter(
    private val context: String,
    callback: HttpRouter.() -> Unit
) : HttpRouter() {

    init {
        this.callback()
    }

    override suspend fun matches(request: Request): Boolean {
        return request.path.startsWith("$context/") && super.matches(request.copy(path = request.path.removePrefix("$context/")))
    }

    override suspend fun call(request: Request): Response? {
        return super.call(request.copy(path = request.path.removePrefix("$context/")))
    }

    override fun getPaths(): List<Pair<String, String>> {
        return super.getPaths().map { Pair(it.first, "$context/${it.second}") }
    }
}

fun HttpRouter.route(path: String, callback: HttpRouter.() -> Unit) {
    handlers.add(HttpSubRouter(path.trim('/'), callback))
}