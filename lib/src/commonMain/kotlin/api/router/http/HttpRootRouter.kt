package com.caseyjbrooks.netlify.api.router.http

class HttpRootRouter(
    initializer: HttpRouter.() -> Unit
) : ApiHandler {

    private val subRouter = HttpSubRouter("", initializer)

    override suspend fun matches(request: Request): Boolean {
        return subRouter.matches(request)
    }

    override suspend fun call(request: Request): Response {
        return subRouter.call(request) ?: Response(404, "Route at '[${request.method}] ${request.path}' not found")
    }

    override fun getPaths(): List<Pair<String, String>> {
        return subRouter.getPaths()
    }

    override fun visit(onVisit: (ApiHandler) -> Unit) {
        onVisit(this)
        subRouter.visit(onVisit)
    }
}