package com.caseyjbrooks.netlify.impl.apps

import com.caseyjbrooks.netlify.api.Clog
import com.caseyjbrooks.netlify.api.expect.runTest
import com.caseyjbrooks.netlify.containsExactly
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

            "@geoffrey bottom 1" to "Printing bottom 1:\n>\n> • one - 1",
            "@geoffrey bottom 3" to "Printing bottom 3:\n>\n> • one - 1\n>\n> • two - 2\n>\n> • three - 3",

            "@geoffrey top 1" to "Printing top 1:\n>\n> • five - 5",
            "@geoffrey top 3" to "Printing top 3:\n>\n> • five - 5\n>\n> • four - 4\n>\n> • three - 3"
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

            "@geoffrey score of @casey" to "casey has 4 points",
            "@geoffrey score of @kris" to "I don't know who kris is...",
            "@kris++" to "kris gained a point and is now at 1",
            "@geoffrey score of @kris" to "kris has 1 points",

            "@geoffrey 1 reasons why @casey" to "Printing 1 reasons why for casey:\n>\n> • 1 for four",
            "@geoffrey 3 reasons why @casey" to "Printing 3 reasons why for casey:\n>\n> • 1 for four\n>\n> • 1 for two\n>\n> • 1 for one",
            "@geoffrey 4 reasons why @casey" to "Printing 4 reasons why for casey:\n>\n> • 1 for four\n>\n> • 1 for two\n>\n> • 1 for one",

            "@geoffrey 1 reasons why @noel" to "I don't know who noel is...",
            "@noel += 10 for being awesome" to "noel gained 10 points for being awesome and is now at 10",
            "@geoffrey 1 reasons why @noel" to "Printing 1 reasons why for noel:\n>\n> • 10 for being awesome"
        )
    }

    @Test
    fun testRoutes() = runTest {
        expectThat(app.getHttpRoutes()).containsExactly(
            "POST" to "/plus-plus/slack/message",
            "POST" to "/plus-plus/teams/message",
            "POST" to "/plus-plus/in-memory/message"
        )
    }

    @Test
    fun testHelpMessages() = runTest {
        expectThat(app.getMessagePatterns()).containsExactly(
            "[user]++ (reason)" to "Add a point to a tagged user",
            "[user]-- (reason)" to "Remove a point to a tagged user",
            "[user] += [score] (reason)" to "Add many points to a tagged user",
            "[user] -= [score] (reason)" to "Remove many points from a tagged user",
            "@geoffrey top [count]" to "Show a leaderboard of the top _n_ players, with the most points",
            "@geoffrey bottom [count]" to "Show a leaderboard of the bottom _n_ players, with the fewest or most negative points",
            "@geoffrey score of [user]" to "Show the current score for a tagged user",
            "@geoffrey [count] reasons why [user]" to "Show the current score for a tagged user, along with a list of the n most recent reasons",
            "@geoffrey halp" to "Show all available commands"
        )
    }

    @Test
    fun testHalp() = runTest {
        expectThat(app.sendTestMessage("@geoffrey halp"))
            .isEqualTo(
            """
            |
            |>
            |> • [user]++ (reason)
            |>
            |> • [user]-- (reason)
            |>
            |> • [user] += [score] (reason)
            |>
            |> • [user] -= [score] (reason)
            |>
            |> • @geoffrey top [count]
            |>
            |> • @geoffrey bottom [count]
            |>
            |> • @geoffrey score of [user]
            |>
            |> • @geoffrey [count] reasons why [user]
            |>
            |> • @geoffrey halp
            """.trimMargin()
        )
    }

}
