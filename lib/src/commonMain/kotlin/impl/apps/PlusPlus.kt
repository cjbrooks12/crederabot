package com.caseyjbrooks.netlify.impl.apps

import com.caseyjbrooks.netlify.api.App
import com.caseyjbrooks.netlify.api.EnvVar
import com.caseyjbrooks.netlify.api.handlers.logging
import com.caseyjbrooks.netlify.api.router.chat.ChatRouter
import com.caseyjbrooks.netlify.api.router.chat.message
import com.caseyjbrooks.netlify.api.router.chat.reply
import com.caseyjbrooks.netlify.api.router.http.HttpChatRouter
import com.caseyjbrooks.netlify.api.router.http.HttpRootRouter
import com.caseyjbrooks.netlify.api.router.http.route
import com.caseyjbrooks.netlify.impl.ProductionEnv
import com.caseyjbrooks.netlify.impl.environments.InMemoryConstants
import com.caseyjbrooks.netlify.impl.environments.MicrosoftTeamsConstants
import com.caseyjbrooks.netlify.impl.environments.SlackConstants
import com.caseyjbrooks.netlify.impl.environments.inMemory
import com.caseyjbrooks.netlify.impl.environments.slack
import com.caseyjbrooks.netlify.impl.environments.teams

class PlusPlusApp(
    override val env: EnvVar = ProductionEnv(),
    val db: PlusPlusDatabase = InMemoryPlusPlusDatabase()
) : App {

    override val router = HttpRootRouter {
        // middleware
        logging()

        // apps
        route("plus-plus") {
            slack {
                plusPlus(SlackConstants.BOT_MENTION, SlackConstants.USERS_MENTION, SlackConstants.THING_MENTION)
            }
            teams {
                plusPlus(MicrosoftTeamsConstants.BOT_MENTION, MicrosoftTeamsConstants.USERS_MENTION, MicrosoftTeamsConstants.THING_MENTION)
            }
            inMemory {
                plusPlus(InMemoryConstants.BOT_MENTION, InMemoryConstants.MENTION)
            }
        }
    }

    private fun ChatRouter.plusPlus(botMention: String, vararg userPatterns: String) {
        userPatterns.forEach { mentioned ->
            message("^$mentioned$PLUS_PLUS$REASON$".toRegex(), {
                simpleMessageFormat = "[user]++ (reason)"
                simpleMessageDescription = "Add a point to a tagged user"
            }) { (user, reason) ->
                val reasonReply = if (reason.isNotBlank()) " for ${reason.trimReason()}" else ""

                val newScore = db.updateScore(user, 1, reason)

                reply("$user gained a point$reasonReply and is now at $newScore")
            }
            message("^$mentioned$MINUS_MINUS$REASON$".toRegex(), {
                simpleMessageFormat = "[user]-- (reason)"
                simpleMessageDescription = "Remove a point to a tagged user"
            }) { (user, reason) ->
                val reasonReply = if (reason.isNotBlank()) " for ${reason.trimReason()}" else ""

                val newScore = db.updateScore(user, -1, reason)

                reply("$user lost a point$reasonReply and is now at $newScore")
            }
            message("^$mentioned$PLUS_EQUAL$REASON$".toRegex(), {
                simpleMessageFormat = "[user] += [score] (reason)"
                simpleMessageDescription = "Add many points to a tagged user"
            }) { (user, amount, reason) ->
                val reasonReply = if (reason.isNotBlank()) " for ${reason.trimReason()}" else ""

                val newScore = db.updateScore(user, amount.toInt(), reason)

                reply("$user gained $amount points$reasonReply and is now at $newScore")
            }
            message("^$mentioned$MINUS_EQUAL$REASON$".toRegex(), {
                simpleMessageFormat = "[user] -= [score] (reason)"
                simpleMessageDescription = "Remove many points from a tagged user"
            }) { (user, amount, reason) ->
                val reasonReply = if (reason.isNotBlank()) " for ${reason.trimReason()}" else ""

                val newScore = db.updateScore(user, -1 * amount.toInt(), reason)

                reply("$user lost $amount points$reasonReply and is now at $newScore")
            }

            message("^${botMention}top (\\d+)$".toRegex(), {
                simpleMessageFormat = "@geoffrey top [count]"
                simpleMessageDescription = "Show a leaderboard of the top _n_ players, with the most points"
            }) { (count) ->
                val topUsers = db.getTopUsers(count.toInt())

                reply("Printing top $count:${topUsers.asSeparatedList("\n>\n> • ") { "${it.first} - ${it.second}" }}")
            }
            message("^${botMention}bottom (\\d+)$".toRegex(), {
                simpleMessageFormat = "@geoffrey bottom [count]"
                simpleMessageDescription = "Show a leaderboard of the bottom _n_ players, with the fewest or most negative points"
            }) { (count) ->
                val bottomUsers = db.getBottomUsers(count.toInt())

                reply("Printing bottom $count:${bottomUsers.asSeparatedList("\n>\n> • ") { "${it.first} - ${it.second}" }}")
            }

            message("^${botMention}score of$mentioned".toRegex(), {
                simpleMessageFormat = "@geoffrey score of [user]"
                simpleMessageDescription = "Show the current score for a tagged user"
            }) { (user) ->
                db.getUserScore(user)
                    ?.let { score -> reply("$user has $score points") }
                    ?: reply("I don't know who $user is...")
            }
            message("^$botMention(\\d*)\\s?reasons why$mentioned$".toRegex(), {
                simpleMessageFormat = "@geoffrey [count] reasons why [user]"
                simpleMessageDescription = "Show the current score for a tagged user, along with a list of the n most recent reasons"
            }) { (count, user) ->
                db.getUserReasons(user, count.toInt())
                    ?.let { reasons -> reply("Printing $count reasons why for $user:${reasons.asSeparatedList("\n>\n> • ") { "${it.first} ${it.second}" }}") }
                    ?: reply("I don't know who $user is...")
            }

            message("^${botMention}halp$".toRegex(), {
                simpleMessageFormat = "@geoffrey halp"
                simpleMessageDescription = "Show all available commands"
            }) {
                reply(getMessagePatterns().asSeparatedList("\n>\n> • ") { it.first })
            }
        }
    }

    private fun String.trimReason(): String {
        return this
            .removePrefix("for")
            .removePrefix("because")
            .trim()
    }

    private fun <T> List<T>.asSeparatedList(sep: String, transform: (T) -> String): String {
        return "$sep${this.joinToString(separator = sep, transform = transform)}"
    }

    suspend fun sendTestMessage(message: String): String {
        return call("POST", "/plus-plus/in-memory/message", message).body?.toString() ?: ""
    }

    fun getHttpRoutes() : List<Pair<String, String>> {
        return router.getPaths()
    }

    fun getMessagePatterns() : List<Pair<String, String>> {
        val matchedPatterns = mutableListOf<Pair<String, String>>()

        router.visit { apiHandler ->
            if(apiHandler is HttpChatRouter) {
                matchedPatterns.addAll(apiHandler.chatRouter.getMessageFormats())
            }
        }

        return matchedPatterns
    }

// Constants
//----------------------------------------------------------------------------------------------------------------------

    companion object {
        const val PLUS_PLUS = "\\+\\+"
        const val MINUS_MINUS = "(?:--|—)"
        const val PLUS_EQUAL = "\\+=\\s*?(\\d+)"
        const val MINUS_EQUAL = "-=\\s*?(\\d+)"
        const val REASON = "(.*)"
    }

}

interface PlusPlusDatabase {

    suspend fun updateScore(user: String, amount: Int, reason: String): Int

    suspend fun getTopUsers(count: Int): List<Pair<String, Int>>

    suspend fun getBottomUsers(count: Int): List<Pair<String, Int>>

    suspend fun getUserScore(user: String): Int?

    suspend fun getUserReasons(user: String, count: Int): List<Pair<Int, String>>?

}

class InMemoryPlusPlusDatabase : PlusPlusDatabase {

    private val usersMap: MutableMap<String, MutableList<Pair<Int, String>>> = mutableMapOf()

    override suspend fun updateScore(user: String, amount: Int, reason: String): Int {
        usersMap
            .getOrPut(user) { mutableListOf() }
            .add(amount to reason)

        return usersMap[user]!!.sumBy { entry -> entry.first }
    }

    override suspend fun getTopUsers(count: Int): List<Pair<String, Int>> {
        return usersMap
            .entries
            .map { it.key to it.value.sumBy { reason -> reason.first } }
            .sortedByDescending { it.second }
            .take(count)
    }

    override suspend fun getBottomUsers(count: Int): List<Pair<String, Int>> {
        return usersMap
            .entries
            .map { it.key to it.value.sumBy { reason -> reason.first } }
            .sortedBy { it.second }
            .take(count)
    }

    override suspend fun getUserScore(user: String): Int? {
        return usersMap[user]
            ?.sumBy { it.first }
    }

    override suspend fun getUserReasons(user: String, count: Int): List<Pair<Int, String>>? {
        return usersMap[user]
            ?.filter { it.second.isNotBlank() }
            ?.takeLast(count)
            ?.reversed()
    }

}
