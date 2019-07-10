package com.caseyjbrooks.netlify.impl.environments

import com.caseyjbrooks.netlify.api.router.chat.ChatContext
import com.caseyjbrooks.netlify.api.router.chat.ChatRootRouter
import com.caseyjbrooks.netlify.api.router.chat.ChatRouter
import com.caseyjbrooks.netlify.api.router.http.HttpRouter
import com.caseyjbrooks.netlify.api.router.http.ok
import com.caseyjbrooks.netlify.api.router.http.post
import com.caseyjbrooks.netlify.api.router.http.route

object SlackConstants {
    const val USERS_MENTION = "\\s*?<@(\\w+)>\\s*?"
    const val THING_MENTION = "\\s*?(.+)\\s*?"
}

fun HttpRouter.slack(messageRouterInit: ChatRouter.()->Unit) {
    route("slack") {
        val chatRouter = ChatRootRouter(messageRouterInit)

        post("message") {
            val body = mapOf<String, String>()
            val context = object : ChatContext {
                override val from = body["from"] ?: ""
                override val originalMessage = body["message"] ?: ""
            }
            chatRouter.handle(context)!!.message.ok()
        }
    }
}
