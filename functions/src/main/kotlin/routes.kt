package com.caseyjbrooks.netlify

import com.caseyjbrooks.netlify.apps.shuffleboard.shuffleboard
import com.caseyjbrooks.netlify.apps.slack.plusPlus
import com.caseyjbrooks.netlify.handlers.logging
import com.caseyjbrooks.netlify.handlers.slackSetup
import com.caseyjbrooks.netlify.handlers.testing
import com.caseyjbrooks.netlify.router.Response
import com.caseyjbrooks.netlify.router.Router
import com.caseyjbrooks.netlify.router.get
import com.caseyjbrooks.netlify.router.slackMessageDefault

fun Router.init() {
    get("sitemap") {
        Response(200, app().printPaths())
    }

    // middleware
    logging()
    testing()

    // slack setup
    slackSetup()

    // apps app
    plusPlus()
    shuffleboard()

    // default slack message handler, to avoid Slack retrying the event
    slackMessageDefault()
}
