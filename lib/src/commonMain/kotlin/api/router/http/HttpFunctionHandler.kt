package com.caseyjbrooks.netlify.api.router.http

class FunctionHandler(
    private val targetMethod: String,
    private val targetPath: String,
    private val handler: suspend Request.(params: List<String>) -> Response
) : ApiHandler {

    override suspend fun matches(request: Request): Boolean {
        return getPathParams(request).first
    }

    override suspend fun call(request: Request): Response {
        return request.handler(getPathParams(request).second)
    }

    override fun getPaths(): List<Pair<String, String>> {
        return listOf(Pair(targetMethod, targetPath))
    }

    private fun getPathParams(request: Request): Pair<Boolean, List<String>> {
        if (request.method != targetMethod) return false to emptyList() // method does not match

        val pathParams = mutableListOf<String>()
        val targetPieces = targetPath.split("/")
        val actualPieces = request.path.split("/")

        if (targetPieces.size != actualPieces.size) return false to emptyList() // path lengths do not match

        val zipped = targetPieces.zip(actualPieces)

        for ((targetPiece, actualPiece) in zipped) {
            if (targetPiece.startsWith(":")) {
                pathParams.add(actualPiece)
            } else {
                if (targetPiece != actualPiece) return false to emptyList() // wrong path
            }
        }

        return true to pathParams
    }
}

fun HttpRouter.get(path: String, callback: suspend Request.(params: List<String>) -> Response) {
    handlers.add(FunctionHandler("GET", path.trim('/'), callback))
}

fun HttpRouter.post(path: String, callback: suspend Request.(params: List<String>) -> Response) {
    handlers.add(FunctionHandler("POST", path.trim('/'), callback))
}

fun HttpRouter.put(path: String, callback: suspend Request.(params: List<String>) -> Response) {
    handlers.add(FunctionHandler("PUT", path.trim('/'), callback))
}

fun HttpRouter.delete(path: String, callback: suspend Request.(params: List<String>) -> Response) {
    handlers.add(FunctionHandler("DELETE", path.trim('/'), callback))
}
