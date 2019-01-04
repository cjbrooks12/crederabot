package router

import FunctionHandler
import Response
import Router
import SLACK_EVENT_CALLBACK_TYPE
import SLACK_WEBHOOK_PATH

fun Router.slack(type: String, callback: (dynamic) -> Response) {
    handlers.add(AnonymousSlackWebhookHandler(type, "POST", SLACK_WEBHOOK_PATH, callback))
}

fun Router.slackEvent(eventType: String, callback: (dynamic) -> Response) {
    handlers.add(AnonymousSlackEventHandler(eventType, "POST", SLACK_WEBHOOK_PATH, callback))
}

open class AnonymousSlackWebhookHandler(
    val type: String,
    method: String,
    path: String,
    val callback: (dynamic) -> Response
) : FunctionHandler(method, path) {

    override fun matches(method: String, path: String, body: dynamic): Boolean {
        return super.matches(method, path, body) && body.type == type
    }

    override fun handle(body: dynamic): Response {
        return callback(body)
    }
}

class AnonymousSlackEventHandler(
    val eventType: String,
    method: String,
    path: String,
    callback: (dynamic) -> Response
) : AnonymousSlackWebhookHandler(SLACK_EVENT_CALLBACK_TYPE, method, path, callback) {

    override fun matches(method: String, path: String, body: dynamic): Boolean {
        return super.matches(method, path, body) && body.event.type == eventType
    }

}