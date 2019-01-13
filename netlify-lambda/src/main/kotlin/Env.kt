package com.caseyjbrooks.netlify


class EnvVar {
    private val _env = js("process.env")

    val firebaseSerivceAccountFile: dynamic = JSON.parse(_env.FIREBASE_SERVICE_ACCOUNT_FILE)
    val firebaseUrl: String = _env.FIREBASE_URL
    val slackVerificationToken: String = _env.SLACK_VERIFICATION_TOKEN
    val slackClientId: String = _env.SLACK_CLIENT_ID
    val slackClientSecret: String = _env.SLACK_CLIENT_SECRET
    val slackRedirectUrl: String = _env.SLACK_REDIRECT_URL

}