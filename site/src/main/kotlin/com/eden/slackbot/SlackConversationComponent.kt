package com.eden.slackbot

import com.eden.orchid.api.OrchidContext
import com.eden.orchid.api.options.annotations.Option
import com.eden.orchid.api.theme.assets.JsPage
import com.eden.orchid.api.theme.components.OrchidComponent
import org.apache.commons.io.FilenameUtils
import javax.inject.Inject

class SlackConversationComponent
@Inject
constructor(
    context: OrchidContext
) : OrchidComponent(context, "slackConversation", 100) {

    @Option
    lateinit var userIcons: List<String>

    @Option
    lateinit var moduleResourcePath: String

    override fun loadAssets() {
        addCss("https://cdnjs.cloudflare.com/ajax/libs/animate.css/3.5.2/animate.min.css")
        addCss("https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css")
        addCss("assets/slack.scss")
        addJs(
            JsPage(
                this,
                "component",
                context.getLocalResourceEntry(moduleResourcePath).apply {
                    reference.isUsePrettyUrl = false
                    reference.path = "assets/js"
                    reference.fileName = FilenameUtils.getBaseName(moduleResourcePath)
                    reference.outputExtension = FilenameUtils.getExtension(moduleResourcePath)
                },
                "js",
                "crederabot-js"
            )
        )
    }
}
