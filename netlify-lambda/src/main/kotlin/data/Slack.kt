package com.caseyjbrooks.netlify.data

import kotlinx.coroutines.await

data class SlackSecureData(
    val appToken: String,
    val botToken: String,
    val botId: String,
    val authorizedBy: String
)

suspend fun getSlackSecureData(teamId: String): SlackSecureData {
    return getFirebaseDatabase()["crederaPlusPlus"][teamId]["secure"].once().then {
        val secure = it.get()
        SlackSecureData(
            appToken = secure.accessToken,
            botToken = secure.botAccessToken,
            botId = secure.botUserId,
            authorizedBy = secure.authorizedBy
        )
    }.await()
}

suspend fun postMessageToSlack(secure: SlackSecureData, channel: String, message: String, attachments: List<String> = emptyList()): dynamic {
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
    return Fetch.fetchJson("https://slack.com/api/chat.postMessage", options).await()
}
