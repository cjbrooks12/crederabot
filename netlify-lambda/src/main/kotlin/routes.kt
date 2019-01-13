package com.caseyjbrooks.netlify

import com.caseyjbrooks.netlify.handlers.plusPlus
import com.caseyjbrooks.netlify.handlers.slackOAuthFlow
import com.caseyjbrooks.netlify.router.get
import com.caseyjbrooks.netlify.router.slackMessageDefault
import com.caseyjbrooks.netlify.router.slackVerification

fun Router.init() {
    get("sitemap") {
        Response(200, app().printPaths())
    }

    // handle Slack URL verification
    slackVerification()

    // handle OAuth flow for registering other apps
    slackOAuthFlow()

    // plus-plus app
    plusPlus()

    // default slack message handler, to avoid Slack retrying the event
    slackMessageDefault()
}