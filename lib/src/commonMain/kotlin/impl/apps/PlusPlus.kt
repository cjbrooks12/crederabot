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
    override val env: EnvVar = ProductionEnv()
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
                reply("$user gained a point$reasonReply")
            }
            message("^$mentioned$MINUS_MINUS$REASON$".toRegex()) { (user, reason) ->
                val reasonReply = if (reason.isNotBlank()) " for ${reason.trimReason()}" else ""
                reply("$user lost a point$reasonReply")
            }
            message("^$mentioned$PLUS_EQUAL$REASON$".toRegex()) { (user, amount, reason) ->
                val reasonReply = if (reason.isNotBlank()) " for ${reason.trimReason()}" else ""
                reply("$user gained $amount points$reasonReply")
            }
            message("^$mentioned$MINUS_EQUAL$REASON$".toRegex()) { (user, amount, reason) ->
                val reasonReply = if (reason.isNotBlank()) " for ${reason.trimReason()}" else ""
                reply("$user lost $amount points$reasonReply")
            }

            message("^top (\\d+)$".toRegex()) { (count) ->
                reply("Printing top $count")
            }
            message("^bottom (\\d+)$".toRegex()) { (count) ->
                reply("Printing bottom $count")
            }

            message("^score of$mentioned".toRegex()) { (user) ->
                reply("Printing score of $user")
            }
            message("^(\\d*)\\s?reasons why$mentioned$".toRegex()) { (count, user) ->
                reply("Printing $count reasons why for $user")
            }
        }
    }

    private fun String.trimReason() : String {
        return this
            .removePrefix("for")
            .removePrefix("because")
            .trim()
    }

    suspend fun sendTestMessage(message: String) : String {
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
