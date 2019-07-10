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
    fun testMessageHandling() = runTest {
        testMessageHandlingArguments().forEach { (sentMessages, expectedReply) ->
            sentMessages.forEach { sentMessage ->
                // pretend that each argument is a new test
                dynamicTest(::setUp) {
                    Clog.i("chat test: sentMessage=[$sentMessage]")
                    expectThat(app.sendTestMessage(sentMessage)).isEqualTo(expectedReply)
                }
            }
        }
    }
    private fun testMessageHandlingArguments(): Map<List<String>, String> {
        return mapOf(
            listOf(
                "@casey++",
                "@casey ++"
            ) to "casey gained a point and is now at 1",
            listOf(
                "@casey--",
                "@casey --",
                "@casey—",
                "@casey —"
            ) to "casey lost a point and is now at -1",
            listOf(
                "@casey++ for reasons",
                "@casey ++ for reasons",
                "@casey++ because reasons",
                "@casey ++ because reasons"
            ) to "casey gained a point for reasons and is now at 1",
            listOf(
                "@casey-- for reasons",
                "@casey -- for reasons",
                "@casey— for reasons",
                "@casey — for reasons",

                "@casey-- because reasons",
                "@casey -- because reasons",
                "@casey— because reasons",
                "@casey — because reasons"
            ) to "casey lost a point for reasons and is now at -1"
        )
    }

    @Test
    fun testIncrDecr() = runTest {
        testIncrDecrArguments().forEach { (sentMessage, expectedReply) ->
            Clog.i("chat test: sentMessage=[$sentMessage]")
            expectThat(app.sendTestMessage(sentMessage)).isEqualTo(expectedReply)
        }
    }
    private fun testIncrDecrArguments(): List<Pair<String, String>> {
        return listOf(
            "@casey++" to "casey gained a point and is now at 1",
            "@casey++" to "casey gained a point and is now at 2",
            "@casey++" to "casey gained a point and is now at 3",
            "@casey++" to "casey gained a point and is now at 4",
            "@casey++" to "casey gained a point and is now at 5",
            "@casey--" to "casey lost a point and is now at 4",
            "@casey--" to "casey lost a point and is now at 3",
            "@casey--" to "casey lost a point and is now at 2",
            "@casey--" to "casey lost a point and is now at 1",
            "@casey++" to "casey gained a point and is now at 2",
            "@kris++" to "kris gained a point and is now at 1",
            "@casey--" to "casey lost a point and is now at 1",
            "@kris--" to "kris lost a point and is now at 0",
            "@kris--" to "kris lost a point and is now at -1",
            "@kris -= 5" to "kris lost 5 points and is now at -6",
            "@kris += 7" to "kris gained 7 points and is now at 1"
        )
    }

    @Test
    fun testTopBottom() = runTest {
        testTopBottomArguments().forEach { (sentMessage, expectedReply) ->
            Clog.i("chat test: sentMessage=[$sentMessage]")
            expectThat(app.sendTestMessage(sentMessage)).isEqualTo(expectedReply)
        }
    }
    private fun testTopBottomArguments(): List<Pair<String, String>> {
        return listOf(
            "@one += 1" to "one gained 1 points and is now at 1",
            "@two += 2" to "two gained 2 points and is now at 2",
            "@three += 3" to "three gained 3 points and is now at 3",
            "@four += 4" to "four gained 4 points and is now at 4",
            "@five += 5" to "five gained 5 points and is now at 5",

            "bottom 1" to "Printing bottom 1:\n> • one - 1",
            "bottom 3" to "Printing bottom 3:\n> • one - 1\n> • two - 2\n> • three - 3",

            "top 1" to "Printing top 1:\n> • five - 5",
            "top 3" to "Printing top 3:\n> • five - 5\n> • four - 4\n> • three - 3"
        )
    }

    @Test
    fun testReasonsWhy() = runTest {
        testReasonsWhyArguments().forEach { (sentMessage, expectedReply) ->
            Clog.i("chat test: sentMessage=[$sentMessage]")
            expectThat(app.sendTestMessage(sentMessage)).isEqualTo(expectedReply)
        }
    }
    private fun testReasonsWhyArguments(): List<Pair<String, String>> {
        return listOf(
            "@casey ++ for one" to "casey gained a point for one and is now at 1",
            "@casey ++ for two" to "casey gained a point for two and is now at 2",
            "@casey ++" to "casey gained a point and is now at 3",
            "@casey ++ for four" to "casey gained a point for four and is now at 4",

            "score of @casey" to "casey has 4 points",
            "score of @kris" to "I don't know who kris is...",
            "@kris++" to "kris gained a point and is now at 1",
            "score of @kris" to "kris has 1 points",

            "1 reasons why @casey" to "Printing 1 reasons why for casey:\n> • 1 for four",
            "3 reasons why @casey" to "Printing 3 reasons why for casey:\n> • 1 for four\n> • 1 for two\n> • 1 for one",
            "4 reasons why @casey" to "Printing 4 reasons why for casey:\n> • 1 for four\n> • 1 for two\n> • 1 for one",

            "1 reasons why @noel" to "I don't know who noel is...",
            "@noel += 10 for being awesome" to "noel gained 10 points for being awesome and is now at 10",
            "1 reasons why @noel" to "Printing 1 reasons why for noel:\n> • 10 for being awesome"
        )
    }

}
