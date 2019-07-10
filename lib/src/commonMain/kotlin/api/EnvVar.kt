package com.caseyjbrooks.netlify.api

interface EnvVar {
    val LOG_REQUESTS: Boolean
    val SLACK_BOT_USERNAME: String
    val SLACK_BOT_ICON: String

    val SLACK_VERIFICATION_TOKEN: String
    val SLACK_CLIENT_ID: String
    val SLACK_CLIENT_SECRET: String
    val SLACK_REDIRECT_URL: String
}
