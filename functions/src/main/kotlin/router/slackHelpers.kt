package com.caseyjbrooks.netlify.router

import com.caseyjbrooks.netlify.app
import com.caseyjbrooks.netlify.apps.slack.USERS_MENTION_NO_CAPTURE
import com.caseyjbrooks.netlify.data.SLACK_EVENT_CALLBACK_APP_MENTION_TYPE
import com.caseyjbrooks.netlify.data.SLACK_EVENT_CALLBACK_MESSAGE_TYPE
import com.caseyjbrooks.netlify.data.SLACK_EVENT_CALLBACK_TYPE
import com.caseyjbrooks.netlify.data.SLACK_WEBHOOK_PATH

// standard Slack API handlers
//----------------------------------------------------------------------------------------------------------------------

fun Router.slack(type: String, callback: suspend (dynamic) -> Response) {
    handlers.add(AnonymousSlackWebhookHandler(type, callback))
}

fun Router.slackMessageDefault() {
    handlers.add(AnonymousSlackMessageHandler(".*".toRegex()) { _, _, _ ->
        Response(
            200,
            "No message matched, but that's OK!"
        )
    })
}

// Handle generic slack messages
//----------------------------------------------------------------------------------------------------------------------

fun Router.slackMessage(messageRegex: Regex, callback: suspend (String, MatchResult, dynamic) -> Response) {
    handlers.add(AnonymousSlackMessageHandler(messageRegex, callback))
}

// Handle messages mentioned at the bot user
//----------------------------------------------------------------------------------------------------------------------

fun Router.slackMention(messageRegex: Regex, callback: suspend (String, MatchResult, dynamic) -> Response) {
    handlers.add(AnonymousSlackMentionHandler("^$USERS_MENTION_NO_CAPTURE${messageRegex.pattern}".toRegex(), callback))
}

fun Router.slackMention(vararg messageStrings: String, callback: suspend (String, MatchResult, dynamic) -> Response) {
    for (messageString in messageStrings) {
        slackMention(messageString.toRegex(), callback)
    }
}

// API handler implementations
//----------------------------------------------------------------------------------------------------------------------

open class AnonymousSlackWebhookHandler(
    private val type: String,
    private val callback: suspend (dynamic) -> Response
) : ApiHandler {

    override suspend fun matches(request: Request): Boolean {
        return request.method == "POST"
                && request.path == SLACK_WEBHOOK_PATH
                && request.body.type == type
    }

    override suspend fun call(request: Request): Response = verify(request) {
        callback(request.body)
    }

    override fun getPaths() = emptyList<Pair<String, String>>()

}

class AnonymousSlackMessageHandler(
    private val messageRegex: Regex,
    val callback: suspend (String, MatchResult, dynamic) -> Response
) : ApiHandler {

    override suspend fun matches(request: Request): Boolean {
        return request.method == "POST"
                && request.path == SLACK_WEBHOOK_PATH
                && request.body.type == SLACK_EVENT_CALLBACK_TYPE
                && request.body.event.type == SLACK_EVENT_CALLBACK_MESSAGE_TYPE
                && messageRegex.matches(request.body.event.text as String)
    }

    override suspend fun call(request: Request): Response = verify(request) {
        callback.invoke(request.body.event.text, messageRegex.matchEntire(request.body.event.text)!!, request.body)
    }

    override fun getPaths() = emptyList<Pair<String, String>>()
}

class AnonymousSlackMentionHandler(
    private val messageRegex: Regex,
    val callback: suspend (String, MatchResult, dynamic) -> Response
) : ApiHandler {

    override suspend fun matches(request: Request): Boolean {
        return request.method == "POST"
                && request.path == SLACK_WEBHOOK_PATH
                && request.body.type == SLACK_EVENT_CALLBACK_TYPE
                && request.body.event.type == SLACK_EVENT_CALLBACK_APP_MENTION_TYPE
                && messageRegex.matches(request.body.event.text as String)
    }

    override suspend fun call(request: Request): Response = verify(request) {
        callback.invoke(request.body.event.text, messageRegex.matchEntire(request.body.event.text)!!, request.body)
    }

    override fun getPaths() = emptyList<Pair<String, String>>()
}

suspend fun verify(request: Request, callback: suspend () -> Response): Response {
    return if (request.body.token == app().env.slackVerificationToken) {
        return callback()
    } else {
        Response(403, "Not Authorized")
    }
}