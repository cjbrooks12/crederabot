package com.caseyjbrooks.netlify

import com.caseyjbrooks.netlify.expect.envBoolean
import com.caseyjbrooks.netlify.expect.envString

class EnvVar {

//    val firebaseSerivceAccountFile: dynamic = JSON.parse(_env.FIREBASE_SERVICE_ACCOUNT_FILE)
//    val firebaseUrl: String = _env.FIREBASE_URL
//    val slackVerificationToken: String = _env.SLACK_VERIFICATION_TOKEN
//    val slackClientId: String = _env.SLACK_CLIENT_ID
//    val slackClientSecret: String = _env.SLACK_CLIENT_SECRET
//    val slackRedirectUrl: String = _env.SLACK_REDIRECT_URL

}

val EnvVar.LOG_REQUESTS by envBoolean()
val EnvVar.SLACK_BOT_USERNAME by envString()
val EnvVar.SLACK_BOT_ICON by envString()
