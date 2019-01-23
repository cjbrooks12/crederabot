package com.caseyjbrooks.netlify.data

import com.caseyjbrooks.netlify.app
import com.caseyjbrooks.netlify.obj
import com.caseyjbrooks.netlify.v
import kotlinx.coroutines.await

const val SLACK_WEBHOOK_PATH = "slack"

const val SLACK_URL_VERIFICATION_TYPE = "url_verification"

const val SLACK_EVENT_CALLBACK_TYPE = "event_callback"
const val SLACK_EVENT_CALLBACK_MESSAGE_TYPE = "message"
const val SLACK_EVENT_CALLBACK_APP_MENTION_TYPE = "app_mention"

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

suspend fun checkDebounceSlackMessage(teamId: String, messageId: String): Boolean {
    val messageIdsRef = getFirebaseDatabase()["crederaPlusPlus"][teamId]["messages"][messageId]
    val data = messageIdsRef.onceNow().get()
    return (data == null)
}

suspend fun logSlackMessageHandled(teamId: String, messageId: String) {
    val messageIdsRef = getFirebaseDatabase()["crederaPlusPlus"][teamId]["messages"][messageId]

    val data = messageIdsRef.onceNow().get()

    if (data == null) {
        // we have not gotten this message before, v it so we don't handle it again
        messageIdsRef.setNow(true)
    }
}

suspend fun postMessageToSlack(secure: SlackSecureData, channelId: String, message: String, attachmentMessages: List<String> = emptyList()): dynamic {
    v("posting message to slack")
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
