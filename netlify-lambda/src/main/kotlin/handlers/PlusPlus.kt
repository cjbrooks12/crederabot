package com.caseyjbrooks.netlify.handlers

import com.caseyjbrooks.netlify.Response
import com.caseyjbrooks.netlify.Router
import com.caseyjbrooks.netlify.data.postMessageToSlack
import com.caseyjbrooks.netlify.router.slackMessage
import kotlinx.coroutines.await

val PLUS_PLUS_USER_REGEX = "<@(\\w+)>\\s*?\\+\\+(.*)".toRegex()
val PLUS_PLUS_THING_REGEX = "(\\w+)\\s*?\\+\\+(.*)".toRegex()
val MINUS_MINUS_USER_REGEX = "<@(\\w+)>\\s*?--(.*)".toRegex()
val MINUS_MINUS_THING_REGEX = "(\\w+)\\s*?--(.*)".toRegex()

fun Router.plusPlus() {
    slackMessage(PLUS_PLUS_USER_REGEX) { _, matchResult, body ->
        val (userid, reason) = matchResult!!.destructured
        Response(200, postMessageToSlack(body.event.channel, "$userid received a point for $reason").await())
    }
    slackMessage(PLUS_PLUS_THING_REGEX) { _, matchResult, body ->
        val (thing, reason) = matchResult!!.destructured
        Response(200, postMessageToSlack(body.event.channel, "$thing received a point for $reason").await())
    }

    slackMessage(MINUS_MINUS_USER_REGEX) { _, matchResult, body ->
        val (userid, reason) = matchResult!!.destructured
        Response(200, postMessageToSlack(body.event.channel, "$userid loses a point for $reason").await())
    }
    slackMessage(MINUS_MINUS_THING_REGEX) { _, matchResult, body ->
        val (thing, reason) = matchResult!!.destructured
        Response(200, postMessageToSlack(body.event.channel, "$thing loses a point for $reason").await())
    }
}

