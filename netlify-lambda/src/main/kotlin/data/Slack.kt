package com.caseyjbrooks.netlify.data

import kotlinx.coroutines.await
import kotlin.js.Promise

const val USER_MENTION = "\\s*?<@(\\w+)>\\s*?"
const val USER_MENTION_NO_CAPTURE = "\\s*?<@\\w+>\\s*?"
const val THING_MENTION = "\\s*?(.+)\\s*?"

data class SlackSecureData(
    val appToken: String,
    val botToken: String,
    val botId: String,
    val authorizedBy: String
)

fun getSlackSecureData(teamId: String): Promise<SlackSecureData> {
    return getFirebaseDatabase()["crederaPlusPlus"][teamId]["secure"].once().then {
        val secure = it.get()
        SlackSecureData(
            appToken = secure.accessToken,
            botToken = secure.botAccessToken,
            botId = secure.botUserId,
            authorizedBy = secure.authorizedBy
        )
    }
}
suspend fun getSlackSecureDataNow(teamId: String): SlackSecureData = getSlackSecureData(teamId).await()

fun getSlackUserInfo(secure: SlackSecureData, userId: String): Promise<dynamic> {
    val options: dynamic = object{}
    options["method"] = "GET"
    options["headers"] = object{}
    options["headers"]["Authorization"] = "Bearer ${secure.appToken}"

    return Fetch.fetchJson("https://slack.com/api/users.profile.get?user=$userId", options)
}

suspend fun getSlackUsername(secure: SlackSecureData, userId: String): String {
    return getSlackUserInfo(secure, userId).then { response: dynamic -> response.profile.real_name_normalized.toString() }.await()
}

fun postMessageToSlack(secure: SlackSecureData, channel: String, message: String, attachments: List<String> = emptyList()): Promise<dynamic> {
    val options: dynamic = object{}
    options["method"] = "POST"

    options["headers"] = object{}
    options["headers"]["Authorization"] = "Bearer ${secure.botToken}"
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
    return Fetch.fetchJson("https://slack.com/api/chat.postMessage", options)
}
suspend fun postMessageToSlackNow(secure: SlackSecureData, channel: String, message: String) = postMessageToSlack(secure, channel, message).await()
