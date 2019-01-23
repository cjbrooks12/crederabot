package com.caseyjbrooks.netlify.apps.slack

import com.caseyjbrooks.netlify.app
import com.caseyjbrooks.netlify.data.FirebaseDatabaseRef
import com.caseyjbrooks.netlify.data.asList
import com.caseyjbrooks.netlify.data.get
import com.caseyjbrooks.netlify.data.getFirebaseDatabase
import com.caseyjbrooks.netlify.data.getSlackSecureData
import com.caseyjbrooks.netlify.data.onceNow
import com.caseyjbrooks.netlify.data.postMessageToSlack
import com.caseyjbrooks.netlify.data.pushNow
import com.caseyjbrooks.netlify.data.setNow
import com.caseyjbrooks.netlify.data.updateNow
import com.caseyjbrooks.netlify.log
import com.caseyjbrooks.netlify.router.Response
import com.caseyjbrooks.netlify.success

suspend fun adjustScore(
    team: String,
    channel: String,
    fromUser: String,
    userId: String,
    isUser: Boolean,
    up: Boolean,
    amount: Int,
    reason: String
): Response {
    log("adjusting score")
    val secure = getSlackSecureData(team)

    when {
        fromUser == userId -> postMessageToSlack(secure, channel, NO.random())
        amount > 10        -> postMessageToSlack(secure, channel, TOO_MANY_POINTS.random())
        else               -> {
            val (userName, newTotal) = createOrUpdateRecord(team, fromUser, userId, isUser, up, amount, reason)

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

            postMessageToSlack(secure, channel, message)
        }
    }

    return success()
}

suspend fun createOrUpdateRecord(
    teamId: String,
    fromUser: String,
    userId: String,
    isUser: Boolean,
    up: Boolean,
    amount: Int,
    reason: String
): Pair<String, Int> {
    log("creating or updating record")
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
            scoreUpdate["username"] = "<@$userId>"
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
    log("showing leaderboard")
    val secure = getSlackSecureData(teamId)

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
    postMessageToSlack(secure, channel, message)

    return success()
}

suspend fun showScore(
    teamId: String,
    channel: String,
    userId: String,
    isUser: Boolean
): Response {
    log("showing score")
    val secure = getSlackSecureData(teamId)
    val user = getFirebaseDatabase()["crederaPlusPlus"][teamId]["users"][userId]

    val data = user.onceNow().get()

    val message: String
    if (data != null) {
        message = "${data.username} has ${data.score} points"
    } else {
        message = "${if (isUser) "Who" else "What"} the %#&* is $userId?!"
    }

    postMessageToSlack(secure, channel, message)

    return success()
}

suspend fun showReasonsWhy(
    teamId: String,
    channel: String,
    userId: String,
    isUser: Boolean,
    count: Int?
): Response {
    log("showing reasons why")
    val secure = getSlackSecureData(teamId)

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

    postMessageToSlack(secure, channel, message)

    return success()
}

suspend fun showHelp(
    teamId: String,
    channel: String
): Response {
    log("showing help")
    val secure = getSlackSecureData(teamId)

    val message = """
        |> [@user/thing]++
        |> [@user/thing]+= [amount]
        |> [@user/thing]--
        |> [@user/thing]-= [amount]
        |> @${app().env.slackBotUsername} top [count]
        |> @${app().env.slackBotUsername} bottom [count]
        |> @${app().env.slackBotUsername} score of [@user/thing]
        |> @${app().env.slackBotUsername} [count?] reasons why [@user/thing]
        |> @${app().env.slackBotUsername} help
    """.trimMargin()

    postMessageToSlack(secure, channel, message)

    return success()
}