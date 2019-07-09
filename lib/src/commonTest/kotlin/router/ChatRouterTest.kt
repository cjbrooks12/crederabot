package com.caseyjbrooks.netlify.router

import com.caseyjbrooks.netlify.expect.runTest
import com.caseyjbrooks.netlify.expectThat
import com.caseyjbrooks.netlify.hasReply
import com.caseyjbrooks.netlify.router.chat.ChatContext
import com.caseyjbrooks.netlify.router.chat.ChatRootRouter
import com.caseyjbrooks.netlify.router.chat.ChatRouter
import com.caseyjbrooks.netlify.router.chat.message
import com.caseyjbrooks.netlify.router.http.reply
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
