package com.caseyjbrooks.netlify.router

import com.caseyjbrooks.netlify.FunctionHandler
import com.caseyjbrooks.netlify.Response
import com.caseyjbrooks.netlify.Router
import com.caseyjbrooks.netlify.SLACK_EVENT_CALLBACK_MESSAGE_TYPE
import com.caseyjbrooks.netlify.SLACK_EVENT_CALLBACK_TYPE
import com.caseyjbrooks.netlify.SLACK_WEBHOOK_PATH
import com.caseyjbrooks.netlify.app

fun Router.slack(type: String, callback: suspend (dynamic) -> Response) {
    handlers.add(AnonymousSlackWebhookHandler(type, callback))
}

fun Router.slackVerification() {
    handlers.add(AnonymousSlackWebhookHandler("url_verification") { body -> Response(200, body.challenge) })
}

fun Router.slackMessageDefault() {
    handlers.add(AnonymousSlackMessageHandler(".*".toRegex()) { _,  _, _ -> Response(200, "No message matched, but that's OK!") })
}

fun Router.slackEvent(eventType: String, callback: suspend (dynamic) -> Response) {
    handlers.add(AnonymousSlackEventHandler(eventType, callback))
}

fun Router.slackMessage(messageRegex: Regex, callback: suspend (String, MatchResult?, dynamic) -> Response) {
    handlers.add(AnonymousSlackMessageHandler(messageRegex, callback))
}

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

suspend fun verify(body: dynamic, callback: suspend () -> Response): Response {
    println("body.token=" + body.token)
    println("app().env.slackVerificationToken=" + app().env.slackVerificationToken)
    return if(body.token == app().env.slackVerificationToken) {
        return callback()
    }
    else {
        Response(403, "Not Authorized")
    }
}