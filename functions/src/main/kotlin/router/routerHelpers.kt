package com.caseyjbrooks.netlify.router

fun Router.get(path: String, callback: suspend (Request) -> Response) {
    handlers.add(FunctionHandler("GET", path, callback))
}

fun Router.post(path: String, callback: suspend (Request) -> Response) {
    handlers.add(FunctionHandler("POST", path, callback))
}

fun Router.put(path: String, callback: suspend (Request) -> Response) {
    handlers.add(FunctionHandler("PUT", path, callback))
}

fun Router.delete(path: String, callback: suspend (Request) -> Response) {
    handlers.add(FunctionHandler("DELETE", path, callback))
}

fun Router.route(path: String, callback: Router.() -> Unit) {
    handlers.add(SubRouter(path, callback))
}

fun Router.before(callback: suspend (Request)-> Pair<Request?, Response?>) {
    beforeMiddlewares.add(object : BeforeMiddleware {
        override suspend fun call(request: Request) = callback(request)
    })
}

fun Router.after(callback: suspend (Request, Response)-> Response) {
    afterMiddlewares.add(object : AfterMiddleware {
        override suspend fun call(request: Request, response: Response) = callback(request, response)
    })
}
