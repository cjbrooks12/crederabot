package com.caseyjbrooks.netlify.impl

import com.caseyjbrooks.netlify.api.EnvVar
import com.caseyjbrooks.netlify.api.expect.envBoolean
import com.caseyjbrooks.netlify.api.expect.envString

class DebugEnv(
    override val LOG_REQUESTS: Boolean = false,
    override val SLACK_BOT_USERNAME: String = "",
    override val SLACK_BOT_ICON: String = "",

    override val SLACK_VERIFICATION_TOKEN: String = "",
    override val SLACK_CLIENT_ID: String = "",
    override val SLACK_CLIENT_SECRET: String = "",
    override val SLACK_REDIRECT_URL: String = ""
) : EnvVar

class ProductionEnv : EnvVar {

    override val LOG_REQUESTS by envBoolean()
    override val SLACK_BOT_USERNAME by envString()
    override val SLACK_BOT_ICON by envString()

    override val SLACK_VERIFICATION_TOKEN by envString()
    override val SLACK_CLIENT_ID by envString()
    override val SLACK_CLIENT_SECRET by envString()
    override val SLACK_REDIRECT_URL by envString()

}