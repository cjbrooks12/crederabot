package com.caseyjbrooks.netlify.impl.environments

import com.caseyjbrooks.netlify.api.router.chat.ChatContext
import com.caseyjbrooks.netlify.api.router.chat.ChatRootRouter
import com.caseyjbrooks.netlify.api.router.chat.ChatRouter
import com.caseyjbrooks.netlify.api.router.http.HttpRouter
import com.caseyjbrooks.netlify.api.router.http.ok
import com.caseyjbrooks.netlify.api.router.http.post
import com.caseyjbrooks.netlify.api.router.http.route

object InMemoryConstants {
    const val BOT_MENTION = "\\s*?@geoffrey\\s*?"
    const val MENTION = "\\s*?@(.+)\\s*?"
}

fun HttpRouter.inMemory(messageRouterInit: ChatRouter.()->Unit) {
    route("in-memory") {
        val chatRouter = ChatRootRouter(messageRouterInit)

        post("message") {
            val context = object : ChatContext {
                override val from = ""
                override val originalMessage = body.toString()
            }
            chatRouter.handle(context)!!.message.ok()
        }
    }
}