package com.caseyjbrooks.netlify.handlers

import com.caseyjbrooks.netlify.Router
import com.caseyjbrooks.netlify.asReponse
import com.caseyjbrooks.netlify.data.USER_MENTION
import com.caseyjbrooks.netlify.data.get
import com.caseyjbrooks.netlify.data.getFirebaseDatabase
import com.caseyjbrooks.netlify.data.getSlackUserInfo
import com.caseyjbrooks.netlify.data.once
import com.caseyjbrooks.netlify.data.postMessageToSlack
import com.caseyjbrooks.netlify.data.push
import com.caseyjbrooks.netlify.data.set
import com.caseyjbrooks.netlify.data.update
import com.caseyjbrooks.netlify.router.slackMention
import com.caseyjbrooks.netlify.router.slackMessage
import kotlin.js.Promise

val PLUS_PLUS_USER_REGEX = "$USER_MENTION\\+\\+(.*)".toRegex()
val PLUS_PLUS_THING_REGEX = "^(.+?)\\s*?\\+\\+(.*)".toRegex()
val MINUS_MINUS_USER_REGEX = "$USER_MENTION--(.*)".toRegex()
val MINUS_MINUS_THING_REGEX = "^(.+?)\\s*?--(.*)".toRegex()

val TOP_X_MENTION_REGEX = ".*?top\\s*?(\\d+)".toRegex()
val BOTTOM_X_MENTION_REGEX = ".*?bottom\\s*?(\\d+)".toRegex()

fun Router.plusPlus() {
    slackMessage(PLUS_PLUS_USER_REGEX) { _, matchResult, body ->
        val (userId, reason) = matchResult!!.destructured
        adjustScore(body.team_id, body.event.channel, body.event.user, userId, true, true, reason).asReponse()
    }
    slackMessage(PLUS_PLUS_THING_REGEX) { _, matchResult, body ->
        val (thing, reason) = matchResult!!.destructured
        adjustScore(body.team_id, body.event.channel, body.event.user, thing, false, true, reason).asReponse()
    }

    slackMessage(MINUS_MINUS_USER_REGEX) { _, matchResult, body ->
        val (userId, reason) = matchResult!!.destructured
        adjustScore(body.team_id, body.event.channel, body.event.user, userId, true, false, reason).asReponse()
    }
    slackMessage(MINUS_MINUS_THING_REGEX) { _, matchResult, body ->
        val (thing, reason) = matchResult!!.destructured
        adjustScore(body.team_id, body.event.channel, body.event.user, thing, false, false, reason).asReponse()
    }

    slackMention(TOP_X_MENTION_REGEX) { _, matchResult, body ->
        val (count) = matchResult!!.destructured
        showTop(body.team_id, body.event.channel, count.toInt()).asReponse()
    }
    slackMention(BOTTOM_X_MENTION_REGEX) { _, matchResult, body ->
        val (count) = matchResult!!.destructured
        showBottom(body.team_id, body.event.channel, count.toInt()).asReponse()
    }
}

private fun adjustScore(
    team: String,
    channel: String,
    fromUser: String,
    userId: String,
    isUser: Boolean,
    up: Boolean,
    reason: String
): Promise<dynamic> {

    return createOrUpdateRecord(team, fromUser, userId, isUser, up, reason)
        .then { newTotal ->
            val messageHeader: Promise<String> = if (isUser)
                getSlackUserInfo(userId).then { response: dynamic ->
                    println("slack user info=" + JSON.stringify(response))
                    response.profile.real_name_normalized.toString()
                }
            else
                Promise.resolve(userId)

            val upMessageTrailer: Promise<String> = if (reason.isBlank())
                Promise.resolve("received a point and is now at $newTotal")
            else
                Promise.resolve("received a point for ${reason.trim()} and is now at $newTotal")

            val downMessageTrailer = if (reason.isBlank())
                Promise.resolve("loses a point and is now at $newTotal")
            else
                Promise.resolve("loses a point for ${reason.trim()} and is now at $newTotal")

            val messageTrailer = if (up) upMessageTrailer else downMessageTrailer

            messageHeader
                .then { header ->
                    messageTrailer.then { trailer ->
                        "$header $trailer"
                    }
                }
                .then { message ->
                    println("post to slack: $message")
                    postMessageToSlack(channel, message)
                }
        }
}

fun createOrUpdateRecord(
    teamId: String,
    fromUser: String,
    userId: String,
    isUser: Boolean,
    up: Boolean,
    reason: String
): Promise<Int> {
    val user = getFirebaseDatabase()["crederaPlusPlus"][teamId][if (isUser) "users" else "things"][userId]

    return user
        .once()
        .then {
            // create or upate the user
            val data = it.get()
            if (data != null) {
                val scoreUpdate: dynamic = object {}
                scoreUpdate["score"] = data.score + if (up) 1 else -1
                user.update(scoreUpdate)
            } else {
                val scoreUpdate: dynamic = object {}
                scoreUpdate["score"] = if (up) 1 else -1
                user.set(scoreUpdate)
            }
        }
        .then {
            // add the reason
            val reasonsUpdate: dynamic = object {}
            reasonsUpdate["delta"] = if (up) 1 else -1
            reasonsUpdate["reason"] = reason.trim()
            reasonsUpdate["from"] = fromUser
            user.child("reasons").push(reasonsUpdate)
        }
        .then {
            // fetch the new score
            user["score"].once()
        }
        .then {
            // return the new score as an int
            (it.get() as Int)
        }
}

fun showTop(
    team: String,
    channel: String,
    amount: Int
): Promise<dynamic> {
    return postMessageToSlack(channel, "Here are the top $amount:")
}

fun showBottom(
    team: String,
    channel: String,
    amount: Int
): Promise<dynamic> {
    return postMessageToSlack(channel, "Here are the bottom $amount:")
}