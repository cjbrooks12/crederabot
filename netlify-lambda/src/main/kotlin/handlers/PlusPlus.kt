package com.caseyjbrooks.netlify.handlers

import com.caseyjbrooks.netlify.Response
import com.caseyjbrooks.netlify.Router
import com.caseyjbrooks.netlify.data.FirebaseDatabaseRef
import com.caseyjbrooks.netlify.data.SlackSecureData
import com.caseyjbrooks.netlify.data.THING_MENTION
import com.caseyjbrooks.netlify.data.USER_MENTION
import com.caseyjbrooks.netlify.data.asList
import com.caseyjbrooks.netlify.data.get
import com.caseyjbrooks.netlify.data.getFirebaseDatabase
import com.caseyjbrooks.netlify.data.getSlackSecureDataNow
import com.caseyjbrooks.netlify.data.getSlackUsername
import com.caseyjbrooks.netlify.data.onceNow
import com.caseyjbrooks.netlify.data.postMessageToSlackNow
import com.caseyjbrooks.netlify.data.pushNow
import com.caseyjbrooks.netlify.data.setNow
import com.caseyjbrooks.netlify.data.updateNow
import com.caseyjbrooks.netlify.router.slackMention
import com.caseyjbrooks.netlify.router.slackMessage
import com.caseyjbrooks.netlify.success

// App handlers
//----------------------------------------------------------------------------------------------------------------------

fun Router.plusPlus() {

    // main ++ and -- messages
    val PLUS_PLUS_USER_REGEX = "^$USER_MENTION\\+\\+(.*)$".toRegex()
    slackMessage(PLUS_PLUS_USER_REGEX) { _, matchResult, body ->
        val (userId, reason) = matchResult!!.destructured
        adjustScore(body.team_id, body.event.channel, body.event.user, userId.trim(), true, true, 1, reason.trim())
    }
    val PLUS_PLUS_THING_REGEX = "^$THING_MENTION\\+\\+(.*)$".toRegex()
    slackMessage(PLUS_PLUS_THING_REGEX) { _, matchResult, body ->
        val (thing, reason) = matchResult!!.destructured
        adjustScore(body.team_id, body.event.channel, body.event.user, thing.trim(), false, true, 1, reason.trim())
    }
    val MINUS_MINUS_USER_REGEX = "^$USER_MENTION--(.*)$".toRegex()
    slackMessage(MINUS_MINUS_USER_REGEX) { _, matchResult, body ->
        val (userId, reason) = matchResult!!.destructured
        adjustScore(body.team_id, body.event.channel, body.event.user, userId.trim(), true, false, 1, reason.trim())
    }
    val MINUS_MINUS_THING_REGEX = "^$THING_MENTION--(.*)$".toRegex()
    slackMessage(MINUS_MINUS_THING_REGEX) { _, matchResult, body ->
        val (thing, reason) = matchResult!!.destructured
        adjustScore(body.team_id, body.event.channel, body.event.user, thing.trim(), false, false, 1, reason.trim())
    }

    // += and -= messages
    val PLUS_EQUAL_USER_REGEX = "^$USER_MENTION\\+=\\s*?(\\d+)\\s+?(.*)$".toRegex()
    slackMessage(PLUS_EQUAL_USER_REGEX) { _, matchResult, body ->
        val (userId, amount, reason) = matchResult!!.destructured
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
    val PLUS_EQUAL_THING_REGEX = "^$THING_MENTION\\+=\\s*?(\\d+)\\s+?(.*)\$".toRegex()
    slackMessage(PLUS_EQUAL_THING_REGEX) { _, matchResult, body ->
        val (thing, amount, reason) = matchResult!!.destructured
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
    val MINUS_EQUAL_USER_REGEX = "^$USER_MENTION-=\\s*?(\\d+)\\s+?(.*)\$".toRegex()
    slackMessage(MINUS_EQUAL_USER_REGEX) { _, matchResult, body ->
        val (userId, amount, reason) = matchResult!!.destructured
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
    val MINUS_EQUAL_THING_REGEX = "^$THING_MENTION-=\\s*?(\\d+)\\s+?(.*)\$".toRegex()
    slackMessage(MINUS_EQUAL_THING_REGEX) { _, matchResult, body ->
        val (thing, amount, reason) = matchResult!!.destructured
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
    val TOP_X_MENTION_REGEX = "top (\\d+)$".toRegex()
    slackMention(TOP_X_MENTION_REGEX) { _, matchResult, body ->
        val (count) = matchResult!!.destructured
        showLeaderboard(body.team_id, body.event.channel, count.toInt(), true)
    }
    val BOTTOM_X_MENTION_REGEX = "bottom (\\d+)$".toRegex()
    slackMention(BOTTOM_X_MENTION_REGEX) { _, matchResult, body ->
        val (count) = matchResult!!.destructured
        showLeaderboard(body.team_id, body.event.channel, count.toInt(), false)
    }
    val USER_SCORE_REGEX = "score of$USER_MENTION$".toRegex()
    slackMention(USER_SCORE_REGEX) { _, matchResult, body ->
        val (userId) = matchResult!!.destructured
        showScore(body.team_id, body.event.channel, userId.trim(), true)
    }
    val THING_SCORE_REGEX = "score of$THING_MENTION$".toRegex()
    slackMention(THING_SCORE_REGEX) { _, matchResult, body ->
        val (thing) = matchResult!!.destructured
        showScore(body.team_id, body.event.channel, thing.trim(), false)
    }

    // show reasons
    val USER_REASONS_WHY_REGEX = "(\\d*)\\s?reasons why$USER_MENTION$".toRegex()
    slackMention(USER_REASONS_WHY_REGEX) { _, matchResult, body ->
        val (count, userId) = matchResult!!.destructured
        showReasonsWhy(body.team_id, body.event.channel, userId.trim(), true, count.toIntOrNull())
    }
    val THING_REASONS_WHY_REGEX = "(\\d+)?\\s?reasons why$THING_MENTION$".toRegex()
    slackMention(THING_REASONS_WHY_REGEX) { _, matchResult, body ->
        val (count, thing) = matchResult!!.destructured
        showReasonsWhy(body.team_id, body.event.channel, thing.trim(), false, count.toIntOrNull())
    }

    // get help
    slackMention("halp", "help") { _, _, body ->
        showHelp(body.team_id, body.event.channel)
    }
}

// App text
//----------------------------------------------------------------------------------------------------------------------

val NO = listOf(
    "No.",
    "Nope.",
    "Absolutely not.",
    "What are you trying to do, bro?",
    "_sigh..._"
)
val TOO_MANY_POINTS = listOf(
    "Oh, so you think I'm just made of money, do you?",
    "You seriously think I'm gonna let you do that?",
    "This is a dangerous road you're walking down.",
    "Too rich for my blood.",
    "I'm gonna make you an offer you can't refuse. No."
)
val POINT_INCREASE = listOf(
    "Congrats!",
    "Let's be friends.",
    "Get yourself something nice.",
    "OK, but don't tell anyone.",
    "I'll do it, but you owe me one."
)
val POINT_DECREASE = listOf(
    "Wow, seriously?",
    "Harsh.",
    "Well fine!",
    "What did they ever do to you?",
    "Now that's just rude."
)
val REASON_BEGINNINGS = listOf(
    "for",
    "because"
)

// App Implementation
//----------------------------------------------------------------------------------------------------------------------

private suspend fun adjustScore(
    team: String,
    channel: String,
    fromUser: String,
    userId: String,
    isUser: Boolean,
    up: Boolean,
    amount: Int,
    reason: String
): Response {
    val secure = getSlackSecureDataNow(team)

    when {
        fromUser == userId -> postMessageToSlackNow(secure, channel, NO.random())
        amount > 10        -> postMessageToSlackNow(secure, channel, TOO_MANY_POINTS.random())
        else               -> {
            val (userName, newTotal) = createOrUpdateRecord(secure, team, fromUser, userId, isUser, up, amount, reason)

            var message = ""

            if (up) {
                message += POINT_INCREASE.random()
            } else {
                message += POINT_DECREASE.random()
            }

            message += "\n> $userName "

            if (amount == 1) {
                if (up) {
                    message += "received a point "
                } else {
                    message += "loses a point "
                }
            } else {
                if (up) {
                    message += "received $amount points "
                } else {
                    message += "loses $amount points "
                }
            }

            if (reason.isBlank()) {
                message += "and is now at $newTotal "
            } else if (reason.isBlank()) {
                message += "and is now at $newTotal "
            } else {
                if(REASON_BEGINNINGS.any { reason.trim().startsWith(it) }) {
                    message += "${reason.trim()} and is now at $newTotal"
                }
                else {
                    message += "for ${reason.trim()} and is now at $newTotal"
                }
            }

            postMessageToSlackNow(secure, channel, message)
        }
    }

    return success()
}

suspend fun createOrUpdateRecord(
    secure: SlackSecureData,
    teamId: String,
    fromUser: String,
    userId: String,
    isUser: Boolean,
    up: Boolean,
    amount: Int,
    reason: String
): Pair<String, Int> {
    val user = getFirebaseDatabase()["crederaPlusPlus"][teamId]["users"][userId]

    val data = user.onceNow().get()

    if (data != null) {
        val scoreUpdate: dynamic = object {}
        scoreUpdate["score"] = data.score + if (up) amount else -1 * amount
        user.updateNow(scoreUpdate)
    } else {
        val scoreUpdate: dynamic = object {}
        scoreUpdate["score"] = if (up) amount else -1 * amount
        scoreUpdate["isUser"] = isUser
        scoreUpdate["userId"] = userId

        // only need to call the Slack API once, and then we can cache it in the db
        if (isUser) {
            scoreUpdate["username"] = getSlackUsername(secure, userId)
        } else {
            scoreUpdate["username"] = userId
        }

        user.setNow(scoreUpdate)
    }

    // add the reason
    val reasonsUpdate: dynamic = object {}
    reasonsUpdate["delta"] = if (up) amount else -1 * amount
    reasonsUpdate["reason"] = reason.trim()
    reasonsUpdate["from"] = fromUser
    user.child("reasons").pushNow(reasonsUpdate)

    // get the username and their new total
    val userData = user.onceNow().get()
    return Pair(userData.username, userData.score)
}

suspend fun showLeaderboard(
    teamId: String,
    channel: String,
    count: Int,
    top: Boolean
): Response {
    val secure = getSlackSecureDataNow(teamId)

    val requestedCount = if (count <= 0) 10 else if (count >= 25) 25 else count

    var usersRef: FirebaseDatabaseRef = getFirebaseDatabase()["crederaPlusPlus"][teamId]["users"]

    usersRef = usersRef.orderByChild("score")

    if (top) {
        usersRef = usersRef.limitToLast(requestedCount)
    } else {
        usersRef = usersRef.limitToFirst(requestedCount)
    }

    val query = usersRef.onceNow()
    val actualCount = query.numChildren()

    val items = mutableListOf<String>()
    query.forEach { userSnapshot ->
        val user = userSnapshot.get()
        items.add("> • ${user.username}: ${user.score}")
        false
    }

    val itemsMessage: String
    if (top) {
        itemsMessage = items.reversed().joinToString("\n")
    } else {
        itemsMessage = items.joinToString("\n")
    }

    val message = "Here are the ${if (top) "top" else "bottom"} $actualCount:\n$itemsMessage"
    postMessageToSlackNow(secure, channel, message)

    return success()
}

suspend fun showScore(
    teamId: String,
    channel: String,
    userId: String,
    isUser: Boolean
): Response {
    val secure = getSlackSecureDataNow(teamId)
    val user = getFirebaseDatabase()["crederaPlusPlus"][teamId]["users"][userId]

    val data = user.onceNow().get()

    val message: String
    if (data != null) {
        message = "${data.username} has ${data.score} points"
    } else {
        message = "${if (isUser) "Who" else "What"} the %#&* is $userId?!"
    }

    postMessageToSlackNow(secure, channel, message)

    return success()
}

suspend fun showReasonsWhy(
    teamId: String,
    channel: String,
    userId: String,
    isUser: Boolean,
    count: Int?
): Response {
    val secure = getSlackSecureDataNow(teamId)

    val requestedCount = if (count != null)
        if (count <= 0) 10 else if (count >= 25) 25 else count
    else
        10

    val user = getFirebaseDatabase()["crederaPlusPlus"][teamId]["users"][userId]

    val data = user.onceNow()

    var message: String
    if (data.exists()) {
        val userData = data.get()
        message = "${userData.username} has ${userData.score} points, here are some raisins:"

        message += data
            .child("reasons")
            .asList(requestedCount, true) { (it.reason as String).isNotEmpty() }
            .joinToString("") {
                if ((it.delta as Int) > 0) {
                    "\n> +1 ${it.reason}"
                } else {
                    "\n> -1 ${it.reason}"
                }
            }
    } else {
        message = "${if (isUser) "Who" else "What"} the %#&* is $userId?!"
    }

    postMessageToSlackNow(secure, channel, message)

    return success()
}

suspend fun showHelp(
    teamId: String,
    channel: String
): Response {
    val secure = getSlackSecureDataNow(teamId)

    val message = """
        |> [@user/thing]++
        |> [@user/thing]+= [amount]
        |> [@user/thing]--
        |> [@user/thing]-= [amount]
        |> @judge-credd top [count]
        |> @judge-credd bottom [count]
        |> @judge-credd score of [@user/thing]
        |> @judge-credd [count?] reasons why [@user/thing]
        |> @judge-credd help/halp
    """.trimMargin()

    postMessageToSlackNow(secure, channel, message)

    return success()
}