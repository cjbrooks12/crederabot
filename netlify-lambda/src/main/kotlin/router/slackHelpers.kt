package com.caseyjbrooks.netlify.router

import com.caseyjbrooks.netlify.FunctionHandler
import com.caseyjbrooks.netlify.Response
import com.caseyjbrooks.netlify.Router
import com.caseyjbrooks.netlify.SLACK_EVENT_CALLBACK_APP_MENTION_TYPE
import com.caseyjbrooks.netlify.SLACK_EVENT_CALLBACK_MESSAGE_TYPE
import com.caseyjbrooks.netlify.SLACK_EVENT_CALLBACK_TYPE
import com.caseyjbrooks.netlify.SLACK_URL_VERIFICATION_TYPE
import com.caseyjbrooks.netlify.SLACK_WEBHOOK_PATH
import com.caseyjbrooks.netlify.app
import com.caseyjbrooks.netlify.data.USER_MENTION_NO_CAPTURE

// standard Slack API handlers
//----------------------------------------------------------------------------------------------------------------------

fun Router.slack(type: String, callback: suspend (dynamic) -> Response) {
    handlers.add(AnonymousSlackWebhookHandler(type, callback))
}
fun Router.slackVerification() {
    handlers.add(AnonymousSlackWebhookHandler(SLACK_URL_VERIFICATION_TYPE) { body -> Response(200, body.challenge) })
}
fun Router.slackOAuthFlow() {

}
fun Router.slackMessageDefault() {
    handlers.add(AnonymousSlackMessageHandler(".*".toRegex()) { _,  _, _ -> Response(200, "No message matched, but that's OK!") })
}
fun Router.slackEvent(eventType: String, callback: suspend (dynamic) -> Response) {
    handlers.add(AnonymousSlackEventHandler(eventType, callback))
}

// Handle generic slack messages
//----------------------------------------------------------------------------------------------------------------------

fun Router.slackMessage(messageRegex: Regex, callback: suspend (String, MatchResult?, dynamic) -> Response) {
    handlers.add(AnonymousSlackMessageHandler(messageRegex, callback))
}
fun Router.slackMessage(vararg messageStrings: String, callback: suspend (String, MatchResult?, dynamic) -> Response) {
    for(messageString in messageStrings) {
        slackMessage(messageString.toRegex(), callback)
    }
}

// Handle messages mentioned at the bot user
//----------------------------------------------------------------------------------------------------------------------

fun Router.slackMention(messageRegex: Regex, callback: suspend (String, MatchResult?, dynamic) -> Response) {
    handlers.add(AnonymousSlackMentionHandler("^$USER_MENTION_NO_CAPTURE${messageRegex.pattern}".toRegex(), callback))
}
fun Router.slackMention(vararg messageStrings: String, callback: suspend (String, MatchResult?, dynamic) -> Response) {
    for(messageString in messageStrings) {
        slackMention(messageString.toRegex(), callback)
    }
}

// API handler implementations
//----------------------------------------------------------------------------------------------------------------------

open class AnonymousSlackWebhookHandler(
    private val type: String,
    private val callback: suspend (dynamic) -> Response
) : FunctionHandler("POST", SLACK_WEBHOOK_PATH) {

    override fun matches(method: String, path: String, body: dynamic): Boolean {
        return super.matches(method, path, body) && body.type == type
    }

    override suspend fun handle(body: dynamic): Response = verify(body) {
        callback(body)
    }
}

open class AnonymousSlackEventHandler(
    private val eventType: String,
    val callback: suspend (dynamic) -> Response
) : FunctionHandler("POST", SLACK_WEBHOOK_PATH) {

    override fun matches(method: String, path: String, body: dynamic): Boolean {
        return super.matches(method, path, body)
                && body.type == SLACK_EVENT_CALLBACK_TYPE
                && body.event.type == eventType
    }

    override suspend fun handle(body: dynamic): Response = verify(body) {
        callback(body)
    }
}

class AnonymousSlackMessageHandler(
    private val messageRegex: Regex,
    val callback: suspend (String, MatchResult?, dynamic) -> Response
) : FunctionHandler("POST", SLACK_WEBHOOK_PATH) {

    override fun matches(method: String, path: String, body: dynamic): Boolean {
        return super.matches(method, path, body)
                && body.type == SLACK_EVENT_CALLBACK_TYPE
                && body.event.type == SLACK_EVENT_CALLBACK_MESSAGE_TYPE
                && messageRegex.matches(body.event.text as String)
    }

    override suspend fun handle(body: dynamic): Response = verify(body) {
        callback.invoke(body.event.text, messageRegex.matchEntire(body.event.text), body)
    }
}

class AnonymousSlackMentionHandler(
    private val messageRegex: Regex,
    val callback: suspend (String, MatchResult?, dynamic) -> Response
) : FunctionHandler("POST", SLACK_WEBHOOK_PATH) {

    override fun matches(method: String, path: String, body: dynamic): Boolean {
        return super.matches(method, path, body)
                && body.type == SLACK_EVENT_CALLBACK_TYPE
                && body.event.type == SLACK_EVENT_CALLBACK_APP_MENTION_TYPE
                && messageRegex.matches(body.event.text as String)
    }

    override suspend fun handle(body: dynamic): Response = verify(body) {
        callback.invoke(body.event.text, messageRegex.matchEntire(body.event.text), body)
    }
}

suspend fun verify(body: dynamic, callback: suspend () -> Response): Response {
    return if(body.token == app().env.slackVerificationToken) {
        return callback()
    }
    else {
        Response(403, "Not Authorized")
    }
}