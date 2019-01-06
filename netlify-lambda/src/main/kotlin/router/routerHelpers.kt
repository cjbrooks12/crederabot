package com.caseyjbrooks.netlify.router

import com.caseyjbrooks.netlify.FunctionHandler
import com.caseyjbrooks.netlify.Response
import com.caseyjbrooks.netlify.Router
import com.caseyjbrooks.netlify.SubRouter

fun Router.get(path: String, callback: (dynamic) -> Response) {
    handlers.add(AnonymousFunctionHandler("GET", path, callback))
}

fun Router.post(path: String, callback: (dynamic) -> Response) {
    handlers.add(AnonymousFunctionHandler("POST", path, callback))
}

fun Router.put(path: String, callback: (dynamic) -> Response) {
    handlers.add(AnonymousFunctionHandler("PUT", path, callback))
}

fun Router.delete(path: String, callback: (dynamic) -> Response) {
    handlers.add(AnonymousFunctionHandler("DELETE", path, callback))
}

class AnonymousFunctionHandler(
    method: String,
    path: String,
    val callback: (dynamic) -> Response
) : FunctionHandler(method, path) {
    override fun handle(body: dynamic): Response = callback(body)
}

fun Router.route(path: String, callback: Router.()->Unit) {
    handlers.add(SubRouter(path, callback))
}