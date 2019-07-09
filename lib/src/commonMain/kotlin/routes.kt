package com.caseyjbrooks.netlify

import com.caseyjbrooks.netlify.handlers.logging
import com.caseyjbrooks.netlify.router.http.HttpRouter

fun HttpRouter.init() {
    // middleware
    logging()

//    slack {
//        slackSetup()     // register API as a Slack bot
//        plusPlus()       // add Plus+ app
//        messageDefault() // default slack message handler, to avoid Slack retrying the event
//    }
//    teams {
//        slackSetup()     // register API as a Microsoft Teams bot
//        plusPlus()       // add Plus+ app
//        messageDefault() // default slack message handler, to avoid Teams retrying the event
//    }
}
