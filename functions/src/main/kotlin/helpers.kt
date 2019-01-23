package com.caseyjbrooks.netlify

import com.caseyjbrooks.netlify.router.Response
import kotlinx.coroutines.await
import kotlin.js.Promise

suspend fun <T> Promise<T>.asReponse(statusCode: Int = 200): Response {
    return Response(statusCode, this.await())
}

fun success(statusCode: Int = 200): Response {
    return Response(statusCode, "")
}
fun error(statusCode: Int = 500): Response {
    return Response(statusCode, "")
}

fun String.asResponse(statusCode: Int = 200): Response {
    return Response(statusCode, this)
}

inline fun obj(block: dynamic.() -> Unit): dynamic {
    return object{}.apply<dynamic>(block)
}

fun log(message: String) {
    if(app().env.logRequests) console.log(message)
}