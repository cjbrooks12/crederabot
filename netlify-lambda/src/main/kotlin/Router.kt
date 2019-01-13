package com.caseyjbrooks.netlify

interface ApiHandler {

    @JsName("matches")
    fun matches(method: String, path: String, body: dynamic): Boolean

    @JsName("call")
    suspend fun call(method: String, path: String, body: dynamic): Response

    fun getPaths() : List<Pair<String, String>>

}

sealed class Router : ApiHandler {

    val handlers: MutableList<ApiHandler> = mutableListOf()

    override fun matches(method: String, path: String, body: dynamic): Boolean {
        return handlers.any { it.matches(method, path , body) }
    }

    override suspend fun call(method: String, path: String, body: dynamic): Response {
        return handlers.first { it.matches(method, path, body) }.call(method, path, body)
    }

    override fun getPaths() : List<Pair<String, String>> {
        return handlers.flatMap { it.getPaths() }
    }

}

class RootRouter(callback: Router.()->Unit) : Router() {

    init {
        this.callback()
    }

    override fun matches(method: String, path: String, body: dynamic): Boolean {
        return true
    }

    override suspend fun call(method: String, path: String, body: dynamic): Response {
        return if(path.startsWith("/")) {
            handlers
                .firstOrNull { it.matches(method, path.removePrefix("/"), body) }
                .let {
                    if (it != null) {
                        it.call(method, path.removePrefix("/"), body)
                    } else {
                        Response(404, "Route at '[$method] $path' not found")
                    }
                }
        }
        else {
            Response(404, "Route must start with leading slash")
        }
    }

    override fun getPaths() : List<Pair<String, String>> {
        return handlers.flatMap { it.getPaths() }.map { Pair(it.first, "/${it.second}") }
    }

}

class SubRouter(val context: String, callback: Router.()->Unit) : Router() {

    init {
        this.callback()
    }

    override fun matches(method: String, path: String, body: dynamic): Boolean {
        return path.startsWith(context) && super.matches(method, path.removePrefix("$context/"), body)
    }

    override suspend fun call(method: String, path: String, body: dynamic): Response {
        return super.call(method, path.removePrefix("$context/"), body)
    }

    override fun getPaths() : List<Pair<String, String>> {
        return super.getPaths().map { Pair(it.first, "$context/${it.second}") }
    }

}

abstract class FunctionHandler(
    val targetMethod: String,
    val targetPath: String
) : ApiHandler {

    override fun matches(method: String, path: String, body: dynamic): Boolean {
        return method == targetMethod && path == targetPath
    }

    override suspend fun call(method: String, path: String, body: dynamic): Response {
        return handle(body)
    }

    protected abstract suspend fun handle(body: dynamic): Response

    override fun getPaths() : List<Pair<String, String>> {
        return listOf(Pair(targetMethod, targetPath))
    }

}

class Response(
    val statusCode: Int,
    val body: dynamic
)

