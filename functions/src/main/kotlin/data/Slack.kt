package com.caseyjbrooks.netlify.data

import com.caseyjbrooks.netlify.app
import com.caseyjbrooks.netlify.obj
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

suspend fun debounceSlackMessage(secure: SlackSecureData, teamId: String, messageId: String): Boolean {
    val messageIdsRef = getFirebaseDatabase()["crederaPlusPlus"][teamId]["messages"][messageId]

    val data = messageIdsRef.onceNow().get()

    return if (data == null) {
        // we have not gotten this message before
        messageIdsRef.setNow(messageId)
        true
    } else {
        // we have already gotten this message before
        false
    }
}

suspend fun postMessageToSlack(secure: SlackSecureData, channelId: String, message: String, attachmentMessages: List<String> = emptyList()): dynamic {
    val options = obj {
        method = "POST"
        headers = obj {
            Authorization = "Bearer ${secure.botToken}"
            this["content-type"] = "application/json"
        }

        body = JSON.stringify(
            obj {
                channel = channelId
                text = message
                as_user = false
                username = app().env.slackBotUsername
                icon_url = app().env.slackBotIcon
                attachments = attachmentMessages.map<String, dynamic> { obj { text = attachment } }.toTypedArray()
            } as Any
        )
    }

    return Fetch.fetchJson("https://slack.com/api/chat.postMessage", options).await()
}
