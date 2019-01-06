package com.caseyjbrooks.netlify.handlers

import com.caseyjbrooks.netlify.Response
import com.caseyjbrooks.netlify.Router
import com.caseyjbrooks.netlify.router.slackMessage

val PLUS_PLUS_USER_REGEX = "<@(\\w+)>\\s*?\\+\\+(.*)".toRegex()
val PLUS_PLUS_THING_REGEX = "(\\w+)\\s*?\\+\\+(.*)".toRegex()
val MINUS_MINUS_USER_REGEX = "<@(\\w+)>\\s*?--(.*)".toRegex()
val MINUS_MINUS_THING_REGEX = "(\\w+)\\s*?--(.*)".toRegex()


fun Router.plusPlus() {
    slackMessage(PLUS_PLUS_USER_REGEX) { _, matchResult, _ ->
        val (userid, reason) = matchResult!!.destructured
        Response(200, "plus plus user: $userid for $reason")
    }
    slackMessage(PLUS_PLUS_THING_REGEX) { _, matchResult, _ ->
        val (thing, reason) = matchResult!!.destructured
        Response(200, "plus plus thing: $thing for $reason")
    }

    slackMessage(MINUS_MINUS_USER_REGEX) { _, matchResult, _ ->
        val (userid, reason) = matchResult!!.destructured
        Response(200, "minus minus user: $userid for $reason")
    }
    slackMessage(MINUS_MINUS_THING_REGEX) { _, matchResult, _ ->
        val (thing, reason) = matchResult!!.destructured
        Response(200, "minus minus thing: $thing for $reason")
    }
}

