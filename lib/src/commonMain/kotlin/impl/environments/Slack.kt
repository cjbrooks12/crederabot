package com.caseyjbrooks.netlify.impl.environments

import com.caseyjbrooks.netlify.api.router.chat.ChatContext
import com.caseyjbrooks.netlify.api.router.chat.ChatRouter
import com.caseyjbrooks.netlify.api.router.http.HttpRouter
import com.caseyjbrooks.netlify.api.router.http.chatRoute
import com.caseyjbrooks.netlify.api.router.http.ok
import com.caseyjbrooks.netlify.api.router.http.post

object SlackConstants {
    const val BOT_MENTION = "\\s*?<@(\\w+)>\\s*?"
    const val USERS_MENTION = "\\s*?<@(\\w+)>\\s*?"
    const val THING_MENTION = "\\s*?(.+)\\s*?"
}

fun HttpRouter.slack(messageRouterInit: ChatRouter.()->Unit) {
    chatRoute("slack", messageRouterInit) { chatRouter ->
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
