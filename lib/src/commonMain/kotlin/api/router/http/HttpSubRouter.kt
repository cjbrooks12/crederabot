package com.caseyjbrooks.netlify.api.router.http

import com.caseyjbrooks.netlify.api.router.chat.ChatRootRouter
import com.caseyjbrooks.netlify.api.router.chat.ChatRouter

open class HttpSubRouter(
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

class HttpChatRouter(
    val chatRouter: ChatRootRouter,
    context: String,
    callback: HttpRouter.() -> Unit
) : HttpSubRouter(context, callback)

fun HttpRouter.route(path: String, callback: HttpRouter.() -> Unit) {
    handlers.add(HttpSubRouter(path.trim('/'), callback))
}

fun HttpRouter.chatRoute(path: String, chatRouter: ChatRootRouter, callback: HttpRouter.(ChatRootRouter) -> Unit) {
    handlers.add(HttpChatRouter(chatRouter, path.trim('/')) { callback(chatRouter) })
}

fun HttpRouter.chatRoute(path: String, messageRouterInit: ChatRouter.()->Unit, callback: HttpRouter.(ChatRootRouter) -> Unit) {
    chatRoute(path, ChatRootRouter(messageRouterInit), callback)
}