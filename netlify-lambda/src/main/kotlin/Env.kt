package com.caseyjbrooks.netlify


class EnvVar {
    private val _env = js("process.env")

    val firebaseSerivceAccountFile: dynamic = JSON.parse(_env.FIREBASE_SERVICE_ACCOUNT_FILE)
    val firebaseUrl: dynamic = _env.FIREBASE_URL
    val slackToken: dynamic = _env.SLACK_TOKEN

}