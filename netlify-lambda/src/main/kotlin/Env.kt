package com.caseyjbrooks.netlify


class EnvVar {
    private val _env = js("process.env")

    val firebaseSerivceAccountFile: dynamic = JSON.parse(_env.FIREBASE_SERVICE_ACCOUNT_FILE)
    val firebaseUrl: String = _env.FIREBASE_URL
    val slackToken: String = _env.SLACK_TOKEN
    val slackVerificationToken: String = _env.SLACK_VERIFICATION_TOKEN

}