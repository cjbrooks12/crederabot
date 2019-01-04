
class EnvVar(val _env: dynamic) {

    val firebaseSerivceAccountFile: dynamic = _env.FIREBASE_SERVICE_ACCOUNT_FILE

    val firebaseUrl: dynamic = _env.FIREBASE_URL

    val slackToken: dynamic = _env.SLACK_TOKEN

}