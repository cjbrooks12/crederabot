package com.caseyjbrooks.netlify.handlers

import com.caseyjbrooks.netlify.Response
import com.caseyjbrooks.netlify.Router
import com.caseyjbrooks.netlify.data.FirebaseDatabaseRef
import com.caseyjbrooks.netlify.data.USER_MENTION
import com.caseyjbrooks.netlify.data.get
import com.caseyjbrooks.netlify.data.getFirebaseDatabase
import com.caseyjbrooks.netlify.data.getSlackUsername
import com.caseyjbrooks.netlify.data.onceNow
import com.caseyjbrooks.netlify.data.postMessageToSlack
import com.caseyjbrooks.netlify.data.postMessageToSlackNow
import com.caseyjbrooks.netlify.data.pushNow
import com.caseyjbrooks.netlify.data.setNow
import com.caseyjbrooks.netlify.data.updateNow
import com.caseyjbrooks.netlify.router.slackMention
import com.caseyjbrooks.netlify.router.slackMessage
import com.caseyjbrooks.netlify.success

val PLUS_PLUS_USER_REGEX = "$USER_MENTION\\+\\+(.*)".toRegex()
val PLUS_PLUS_THING_REGEX = "^(.+?)\\s*?\\+\\+(.*)".toRegex()
val MINUS_MINUS_USER_REGEX = "$USER_MENTION--(.*)".toRegex()
val MINUS_MINUS_THING_REGEX = "^(.+?)\\s*?--(.*)".toRegex()

val TOP_X_MENTION_REGEX = ".*?top\\s*?(\\d+)".toRegex()
val BOTTOM_X_MENTION_REGEX = ".*?bottom\\s*?(\\d+)".toRegex()

fun Router.plusPlus() {
    slackMessage(PLUS_PLUS_USER_REGEX) { _, matchResult, body ->
        val (userId, reason) = matchResult!!.destructured
        adjustScore(body.team_id, body.event.channel, body.event.user, userId, true, true, reason)
    }
    slackMessage(PLUS_PLUS_THING_REGEX) { _, matchResult, body ->
        val (thing, reason) = matchResult!!.destructured
        adjustScore(body.team_id, body.event.channel, body.event.user, thing, false, true, reason)
    }

    slackMessage(MINUS_MINUS_USER_REGEX) { _, matchResult, body ->
        val (userId, reason) = matchResult!!.destructured
        adjustScore(body.team_id, body.event.channel, body.event.user, userId, true, false, reason)
    }
    slackMessage(MINUS_MINUS_THING_REGEX) { _, matchResult, body ->
        val (thing, reason) = matchResult!!.destructured
        adjustScore(body.team_id, body.event.channel, body.event.user, thing, false, false, reason)
    }

    slackMention(TOP_X_MENTION_REGEX) { _, matchResult, body ->
        val (count) = matchResult!!.destructured
        showLeaderboard(body.team_id, body.event.channel, count.toInt(), true)
    }
    slackMention(BOTTOM_X_MENTION_REGEX) { _, matchResult, body ->
        val (count) = matchResult!!.destructured
        showLeaderboard(body.team_id, body.event.channel, count.toInt(), false)
    }
}

private suspend fun adjustScore(
    team: String,
    channel: String,
    fromUser: String,
    userId: String,
    isUser: Boolean,
    up: Boolean,
    reason: String
): Response {
    val (userName, newTotal) = createOrUpdateRecord(team, fromUser, userId, isUser, up, reason)

    var message = "$userName "

    if(up) {
        message += "received a point "
    }
    else {
        message += "loses a point "
    }

    if (reason.isBlank()){
        message += "and is now at $newTotal "
    }
    else {
        message += "for ${reason.trim()} and is now at $newTotal"
    }

    postMessageToSlackNow(channel, message)

    return success()
}

suspend fun createOrUpdateRecord(
    teamId: String,
    fromUser: String,
    userId: String,
    isUser: Boolean,
    up: Boolean,
    reason: String
): Pair<String, Int> {
    val user = getFirebaseDatabase()["crederaPlusPlus"][teamId]["users"][userId]

    val data = user.onceNow().get()

    if (data != null) {
        val scoreUpdate: dynamic = object {}
        scoreUpdate["score"] = data.score + if (up) 1 else -1
        user.updateNow(scoreUpdate)
    } else {
        val scoreUpdate: dynamic = object {}
        scoreUpdate["score"] = if (up) 1 else -1
        scoreUpdate["isUser"] = isUser

        // only need to call the Slack API once, and then we can cache it in the db
        if (isUser) {
            scoreUpdate["username"] = getSlackUsername(userId)
        }
        else {
            scoreUpdate["username"] = userId
        }

        user.setNow(scoreUpdate)
    }

    // add the reason
    val reasonsUpdate: dynamic = object {}
    reasonsUpdate["delta"] = if (up) 1 else -1
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
    val requestedCount = if(count <= 0) 10 else if(count >= 25) 25 else count

    var usersRef: FirebaseDatabaseRef = getFirebaseDatabase()["crederaPlusPlus"][teamId]["users"]

    usersRef = usersRef.orderByChild("score")

    if(top) {
        usersRef = usersRef.limitToLast(requestedCount)
    }
    else {
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
    if(top) {
        itemsMessage = items.reversed().joinToString("\n")
    }
    else {
        itemsMessage = items.joinToString("\n")
    }

    val message = "Here are the ${if(top) "top" else "bottom"} $actualCount:\n$itemsMessage"
    postMessageToSlack(channel, message)

    return success()
}

