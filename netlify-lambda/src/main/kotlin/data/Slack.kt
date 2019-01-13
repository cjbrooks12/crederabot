package com.caseyjbrooks.netlify.data

import com.caseyjbrooks.netlify.app
import kotlinx.coroutines.await
import kotlin.js.Promise

const val USER_MENTION = "\\s*?<@(\\w+)>\\s*?"
const val USER_MENTION_NO_CAPTURE = "\\s*?<@\\w+>\\s*?"
const val THING_MENTION = "\\s*?(.+)\\s*?"

fun getSlackUserInfo(userId: String): Promise<dynamic> {
    val options: dynamic = object{}
    options["method"] = "GET"
    options["headers"] = object{}
    options["headers"]["Authorization"] = "Bearer ${app().env.slackAppToken}"

    return Fetch
        .fetch("https://slack.com/api/users.profile.get?user=$userId", options)
        .then<dynamic> { it.json() }
}

suspend fun getSlackUsername(userId: String): String {
    return getSlackUserInfo(userId).then { response: dynamic -> response.profile.real_name_normalized.toString() }.await()
}

fun getSlackUsers(): Promise<dynamic> {
    val options: dynamic = object{}
    options["method"] = "GET"
    options["headers"] = object{}
    options["headers"]["Authorization"] = "Bearer ${app().env.slackAppToken}"

    return Fetch
        .fetch("https://slack.com/api/users.list", options)
        .then<dynamic> { it.json() }
}

fun postMessageToSlack(channel: String, message: String, attachments: List<String> = emptyList()): Promise<dynamic> {
    val options: dynamic = object{}
    options["method"] = "POST"

    options["headers"] = object{}
    options["headers"]["Authorization"] = "Bearer ${app().env.slackBotToken}"
    options["headers"]["content-type"] = "application/json"

    options["body"] = object{}
    options["body"]["channel"] = channel
    options["body"]["text"] = message

    if(attachments.isNotEmpty()) {
        val attachmentsArray = mutableListOf<dynamic>()

        for(attachment in attachments) {
            val attachmentObject: dynamic = object{}
            attachmentObject["text"] = attachment
            attachmentsArray.add(attachmentObject)
        }
        options["body"]["attachments"] = attachmentsArray.toTypedArray()
    }

    options["body"] = JSON.stringify(options["body"])
    return Fetch
        .fetch("https://slack.com/api/chat.postMessage", options)
        .then<dynamic> { it.json() }
}
suspend fun postMessageToSlackNow(channel: String, message: String) = postMessageToSlack(channel, message).await()