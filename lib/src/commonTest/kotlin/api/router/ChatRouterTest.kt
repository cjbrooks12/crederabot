package com.caseyjbrooks.netlify.api.router

import com.caseyjbrooks.netlify.api.expect.runTest
import com.caseyjbrooks.netlify.api.router.chat.ChatContext
import com.caseyjbrooks.netlify.api.router.chat.ChatRootRouter
import com.caseyjbrooks.netlify.api.router.chat.ChatRouter
import com.caseyjbrooks.netlify.api.router.chat.message
import com.caseyjbrooks.netlify.api.router.chat.reply
import com.caseyjbrooks.netlify.expectThat
import com.caseyjbrooks.netlify.hasReply
import kotlin.test.Test

class ChatRouterTest {

    @Test
    fun testNormalAppRoutes() = runTest {
        val router = ChatRootRouter {
            plusPlus()
        }

        expectThat(router.handle(TestChatContext("test", "casey++"))).hasReply("casey gained a point")
        expectThat(router.handle(TestChatContext("test", "casey--"))).hasReply("casey lost a point")
        expectThat(router.handle(TestChatContext("test", "casey++ for reasons"))).hasReply("casey gained a point for for reasons")
        expectThat(router.handle(TestChatContext("test", "casey-- for reasons"))).hasReply("casey lost a point for for reasons")
    }

    private fun ChatRouter.plusPlus() {
        message("(.*?)\\+\\+".toRegex()) { (user) ->
            reply("$user gained a point")
        }
        message("(.*?)--".toRegex()) { (user) ->
            reply("$user lost a point")
        }

        message("(.*?)\\+\\+(.*)".toRegex()) { (user, reason) ->
            reply("$user gained a point for ${reason.trim()}")
        }
        message("(.*?)--(.*)".toRegex()) { (user, reason) ->
            reply("$user lost a point for ${reason.trim()}")
        }
    }

    class TestChatContext(
        override val from: String,
        override val originalMessage: String
    ) : ChatContext

}
