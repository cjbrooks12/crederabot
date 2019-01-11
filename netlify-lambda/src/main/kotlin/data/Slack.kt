package com.caseyjbrooks.netlify.data

import com.caseyjbrooks.netlify.app
import kotlin.js.Promise

fun getSlackUserInfo(userId: String): Promise<dynamic> {
    val options: dynamic = object{}
    options["method"] = "GET"
    options["headers"] = object{}
    options["headers"]["Authorization"] = "Bearer ${app().env.slackToken}"

    return Fetch
        .fetch("https://slack.com/api/users.profile.get?user=$userId", options)
        .then<dynamic> { it.json() }
}

fun getSlackUsers(): Promise<dynamic> {
    val options: dynamic = object{}
    options["method"] = "GET"
    options["headers"] = object{}
    options["headers"]["Authorization"] = "Bearer ${app().env.slackToken}"

    return Fetch
        .fetch("https://slack.com/api/users.list", options)
        .then<dynamic> { it.json() }
}

fun postMessageToSlack(channel: String, message: String): Promise<dynamic> {
    val options: dynamic = object{}
    options["method"] = "POST"

    options["headers"] = object{}
    options["headers"]["Authorization"] = "Bearer ${app().env.slackToken}"
    options["headers"]["content-type"] = "application/json"

    options["body"] = object{}
    options["body"]["channel"] = channel
    options["body"]["text"] = message
    options["body"] = JSON.stringify(options["body"])

    return Fetch
        .fetch("https://slack.com/api/chat.postMessage", options)
        .then<dynamic> { it.json() }
}
