package com.caseyjbrooks.netlify.apps.slack

import com.caseyjbrooks.netlify.Router
import com.caseyjbrooks.netlify.router.slackMention
import com.caseyjbrooks.netlify.router.slackMessage

fun Router.plusPlus() {

    // ++ and -- messages
    slackMessage(PLUS_PLUS_USERS_REGEX) { _, matchResult, body ->
        val (userId, reason) = matchResult.destructured
        adjustScore(body.team_id, body.event.channel, body.event.user, userId.trim(), true, true, 1, reason.trim())
    }
    slackMessage(PLUS_PLUS_THING_REGEX) { _, matchResult, body ->
        val (thing, reason) = matchResult.destructured
        adjustScore(body.team_id, body.event.channel, body.event.user, thing.trim(), false, true, 1, reason.trim())
    }
    slackMessage(MINUS_MINUS_USERS_REGEX) { _, matchResult, body ->
        val (userId, reason) = matchResult.destructured
        adjustScore(body.team_id, body.event.channel, body.event.user, userId.trim(), true, false, 1, reason.trim())
    }
    slackMessage(MINUS_MINUS_THING_REGEX) { _, matchResult, body ->
        val (thing, reason) = matchResult.destructured
        adjustScore(body.team_id, body.event.channel, body.event.user, thing.trim(), false, false, 1, reason.trim())
    }

    // += and -= messages
    slackMessage(PLUS_EQUAL_USERS_REGEX) { _, matchResult, body ->
        val (userId, amount, reason) = matchResult.destructured
        adjustScore(
            body.team_id,
            body.event.channel,
            body.event.user,
            userId.trim(),
            true,
            true,
            amount.toInt(),
            reason.trim()
        )
    }
    slackMessage(PLUS_EQUAL_THING_REGEX) { _, matchResult, body ->
        val (thing, amount, reason) = matchResult.destructured
        adjustScore(
            body.team_id,
            body.event.channel,
            body.event.user,
            thing.trim(),
            false,
            true,
            amount.toInt(),
            reason.trim()
        )
    }
    slackMessage(MINUS_EQUAL_USERS_REGEX) { _, matchResult, body ->
        val (userId, amount, reason) = matchResult.destructured
        adjustScore(
            body.team_id,
            body.event.channel,
            body.event.user,
            userId.trim(),
            true,
            false,
            amount.toInt(),
            reason.trim()
        )
    }
    slackMessage(MINUS_EQUAL_THING_REGEX) { _, matchResult, body ->
        val (thing, amount, reason) = matchResult.destructured
        adjustScore(
            body.team_id,
            body.event.channel,
            body.event.user,
            thing.trim(),
            false,
            false,
            amount.toInt(),
            reason.trim()
        )
    }

    // show leaderboards, either from the top or the bottom, or for a single user
    slackMention(TOP_X_MENTION_REGEX) { _, matchResult, body ->
        val (count) = matchResult.destructured
        showLeaderboard(body.team_id, body.event.channel, count.toInt(), true)
    }
    slackMention(BOTTOM_X_MENTION_REGEX) { _, matchResult, body ->
        val (count) = matchResult.destructured
        showLeaderboard(body.team_id, body.event.channel, count.toInt(), false)
    }
    slackMention(USER_SCORE_REGEX) { _, matchResult, body ->
        val (userId) = matchResult.destructured
        showScore(body.team_id, body.event.channel, userId.trim(), true)
    }
    slackMention(THING_SCORE_REGEX) { _, matchResult, body ->
        val (thing) = matchResult.destructured
        showScore(body.team_id, body.event.channel, thing.trim(), false)
    }

    // show reasons
    slackMention(USER_REASONS_WHY_REGEX) { _, matchResult, body ->
        val (count, userId) = matchResult.destructured
        showReasonsWhy(body.team_id, body.event.channel, userId.trim(), true, count.toIntOrNull())
    }
    slackMention(THING_REASONS_WHY_REGEX) { _, matchResult, body ->
        val (count, thing) = matchResult.destructured
        showReasonsWhy(body.team_id, body.event.channel, thing.trim(), false, count.toIntOrNull())
    }

    // get help
    slackMention("halp", "help") { _, _, body ->
        showHelp(body.team_id, body.event.channel)
    }
}