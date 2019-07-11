package com.eden.slackbot

import com.caseyjbrooks.netlify.impl.DebugEnv
import com.caseyjbrooks.netlify.impl.apps.PlusPlusApp
import com.eden.orchid.api.OrchidContext
import com.eden.orchid.api.theme.components.OrchidComponent
import javax.inject.Inject

class ChatbotDocumentationComponent
@Inject
constructor(
    context: OrchidContext
) : OrchidComponent(context, "chatbotDocumentation", 100) {

    private val app by lazy {
        PlusPlusApp(DebugEnv())
    }

    val routes by lazy {
        app.getHttpRoutes()
    }

    val messages by lazy {
        app.getMessagePatterns()
    }
}
