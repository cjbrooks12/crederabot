package com.caseyjbrooks.netlify.impl.apps

import com.caseyjbrooks.netlify.api.App
import com.caseyjbrooks.netlify.api.EnvVar
import com.caseyjbrooks.netlify.api.handlers.logging
import com.caseyjbrooks.netlify.api.router.chat.ChatRouter
import com.caseyjbrooks.netlify.api.router.chat.message
import com.caseyjbrooks.netlify.api.router.chat.reply
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
                plusPlus(SlackConstants.USERS_MENTION, SlackConstants.THING_MENTION)
            }
            teams {
                plusPlus(MicrosoftTeamsConstants.USERS_MENTION, MicrosoftTeamsConstants.THING_MENTION)
            }
            inMemory {
                plusPlus(InMemoryConstants.MENTION)
            }
        }
    }

    private fun ChatRouter.plusPlus(vararg userPatterns: String) {
        userPatterns.forEach { mentioned ->
            message("^$mentioned$PLUS_PLUS$REASON$".toRegex()) { (user, reason) ->
                val reasonReply = if (reason.isNotBlank()) " for ${reason.trimReason()}" else ""

                val newScore = db.updateScore(user, 1, reason)

                reply("$user gained a point$reasonReply and is now at $newScore")
            }
            message("^$mentioned$MINUS_MINUS$REASON$".toRegex()) { (user, reason) ->
                val reasonReply = if (reason.isNotBlank()) " for ${reason.trimReason()}" else ""

                val newScore = db.updateScore(user, -1, reason)

                reply("$user lost a point$reasonReply and is now at $newScore")
            }
            message("^$mentioned$PLUS_EQUAL$REASON$".toRegex()) { (user, amount, reason) ->
                val reasonReply = if (reason.isNotBlank()) " for ${reason.trimReason()}" else ""

                val newScore = db.updateScore(user, amount.toInt(), reason)

                reply("$user gained $amount points$reasonReply and is now at $newScore")
            }
            message("^$mentioned$MINUS_EQUAL$REASON$".toRegex()) { (user, amount, reason) ->
                val reasonReply = if (reason.isNotBlank()) " for ${reason.trimReason()}" else ""

                val newScore = db.updateScore(user, -1 * amount.toInt(), reason)

                reply("$user lost $amount points$reasonReply and is now at $newScore")
            }

            message("^top (\\d+)$".toRegex()) { (count) ->
                val topUsers = db.getTopUsers(count.toInt())

                reply("Printing top $count:${topUsers.asSeparatedList("\n> • ") { "${it.first} - ${it.second}" }}")
            }
            message("^bottom (\\d+)$".toRegex()) { (count) ->
                val bottomUsers = db.getBottomUsers(count.toInt())

                reply("Printing bottom $count:${bottomUsers.asSeparatedList("\n> • ") { "${it.first} - ${it.second}" }}")
            }

            message("^score of$mentioned".toRegex()) { (user) ->
                db.getUserScore(user)
                    ?.let { score -> reply("$user has $score points") }
                    ?: reply("I don't know who $user is...")
            }
            message("^(\\d*)\\s?reasons why$mentioned$".toRegex()) { (count, user) ->
                db.getUserReasons(user, count.toInt())
                    ?.let { reasons -> reply("Printing $count reasons why for $user:${reasons.asSeparatedList("\n> • ") { "${it.first} ${it.second}" }}") }
                    ?: reply("I don't know who $user is...")
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