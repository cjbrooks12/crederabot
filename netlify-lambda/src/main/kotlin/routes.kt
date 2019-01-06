package com.caseyjbrooks.netlify

import com.caseyjbrooks.netlify.handlers.plusPlus
import com.caseyjbrooks.netlify.router.get
import com.caseyjbrooks.netlify.router.slackVerification

fun Router.init() {
    get("sitemap") {
        Response(200, app().printPaths())
    }

    // handle Slack URL verification
    slackVerification()

    // plus-plus app
    plusPlus()
}