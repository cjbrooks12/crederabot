package com.caseyjbrooks.netlify.router

data class Request(
    val method: String,
    val path: String,
    val body: dynamic
)

data class Response(
    val statusCode: Int,
    val body: dynamic
)

interface ApiHandler {

    /**
     * Check if any of this handler or any of its children can handle the request
     */
    suspend fun matches(request: Request): Boolean

    /**
     * Given that this handler or one of its children can handle the request, handle it now
     */
    suspend fun call(request: Request): Response?

    fun getPaths(): List<Pair<String, String>>

}

sealed class Router : ApiHandler {

    val handlers: MutableList<ApiHandler> = mutableListOf()
    val beforeMiddlewares: MutableList<BeforeMiddleware> = mutableListOf()
    val afterMiddlewares: MutableList<AfterMiddleware> = mutableListOf()

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
        var actualRequest = request
        var actualResponse: Response? = null

        for (middleware in beforeMiddlewares) {
            val middlewareResult = middleware.call(actualRequest)
            if (middlewareResult.first != null) {
                actualRequest = middlewareResult.first!!
            } else if (middlewareResult.second != null) {
                actualResponse = middlewareResult.second!!
                break
            }
        }
        return Pair(actualRequest, actualResponse)
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

class SubRouter(private val context: String, callback: Router.() -> Unit) : Router() {

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

class RootRouter constructor(
    initializer: Router.() -> Unit
) : ApiHandler {

    val subRouter = SubRouter("", initializer)

    override suspend fun matches(request: Request): Boolean {
        return subRouter.matches(request)
    }

    override suspend fun call(request: Request): Response {
        return subRouter.call(request) ?: Response(404, "Route at '[${request.method}] ${request.path}' not found")
    }

    override fun getPaths(): List<Pair<String, String>> {
        return subRouter.getPaths()
    }
}

class FunctionHandler(
    val targetMethod: String,
    val targetPath: String,
    val handler: suspend (Request) -> Response
) : ApiHandler {

    override suspend fun matches(request: Request): Boolean {
        return request.method == targetMethod && request.path == targetPath
    }

    override suspend fun call(request: Request): Response {
        return handler(request)
    }

    override fun getPaths(): List<Pair<String, String>> {
        return listOf(Pair(targetMethod, targetPath))
    }

}



