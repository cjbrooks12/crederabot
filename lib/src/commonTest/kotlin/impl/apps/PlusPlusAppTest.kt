package com.caseyjbrooks.netlify.impl.apps

import com.caseyjbrooks.netlify.api.Clog
import com.caseyjbrooks.netlify.api.expect.runTest
import com.caseyjbrooks.netlify.dynamicTest
import com.caseyjbrooks.netlify.expectThat
import com.caseyjbrooks.netlify.impl.DebugEnv
import com.caseyjbrooks.netlify.isEqualTo
import kotlin.test.BeforeTest
import kotlin.test.Test

class PlusPlusAppTest {

    lateinit var app: PlusPlusApp

    @BeforeTest
    fun setUp() {
        app = PlusPlusApp(DebugEnv())
    }

    @Test
    fun testNormalAppRoutes() = runTest {
        arguments().forEach { (sentMessages, expectedReply) ->
            sentMessages.forEach { sentMessage ->
                // pretend that each argument is a new test
                dynamicTest(::setUp) {
                    Clog.i("chat test: sentMessage=[$sentMessage]")
                    expectThat(app.sendTestMessage(sentMessage)).isEqualTo(expectedReply)
                }
            }
        }
    }

    private fun arguments(): Map<List<String>, String> {
        return mapOf(
            listOf(
                "@casey++",
                "@casey ++"
            ) to "casey gained a point",
            listOf(
                "@casey--",
                "@casey --",
                "@casey—",
                "@casey —"
            ) to "casey lost a point",
            listOf(
                "@casey++ for reasons",
                "@casey ++ for reasons",
                "@casey++ because reasons",
                "@casey ++ because reasons"
            ) to "casey gained a point for reasons",
            listOf(
                "@casey-- for reasons",
                "@casey -- for reasons",
                "@casey— for reasons",
                "@casey — for reasons",

                "@casey-- because reasons",
                "@casey -- because reasons",
                "@casey— because reasons",
                "@casey — because reasons"
            ) to "casey lost a point for reasons"
        )
    }

}
