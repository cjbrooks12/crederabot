package com.caseyjbrooks.netlify.impl

import com.caseyjbrooks.netlify.api.App
import com.caseyjbrooks.netlify.api.EnvVar
import com.caseyjbrooks.netlify.api.handlers.logging
import com.caseyjbrooks.netlify.api.router.http.HttpRootRouter

class PlusPlusApp(
    override val env: EnvVar = ProductionEnv()
) : App {

    override val router = HttpRootRouter {
        // middleware
        logging()

//        slack {
//            slackSetup()     // register API as a Slack bot
//            plusPlus()       // add Plus+ app
//            messageDefault() // default slack message handler, to avoid Slack retrying the event
//        }
//        teams {
//            slackSetup()     // register API as a Microsoft Teams bot
//            plusPlus()       // add Plus+ app
//            messageDefault() // default slack message handler, to avoid Teams retrying the event
//        }
    }
}
