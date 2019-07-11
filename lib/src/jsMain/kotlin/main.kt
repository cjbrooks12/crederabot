package com.caseyjbrooks.netlify

import com.caseyjbrooks.netlify.api.Clog
import com.caseyjbrooks.netlify.impl.DebugEnv
import com.caseyjbrooks.netlify.impl.apps.PlusPlusApp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.html.div
import kotlinx.html.dom.create
import kotlinx.html.img
import kotlinx.html.span
import kotlinx.html.unsafe
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import kotlin.browser.document
import kotlin.browser.window
import kotlin.js.Date


fun main() {
    Clog.v("main() called from Kotlin crederabot module")
    val app = PlusPlusApp(DebugEnv())
    js("window.plusPlusApp = app")

    val form = document.querySelector("form.input-wrapper")!!
    val formField = document.querySelector("form.input-wrapper input")!! as HTMLInputElement

    ChatUser.Geoffrey.appendMessage("Morning everyone!")
    ChatUser.KyleD.appendMessage("Yooooo")
    ChatUser.Sami.appendMessage("Dude, I found this awesome pen")
    ChatUser.Sami.appendMessage("One sec, going to find the link")
    ChatUser.Sami.appendMessage("Got it! [Slack Chat CSS](#)")
    ChatUser.Geoffrey.appendMessage("Sick! I'm so hearting that and posting on Twitter.")

    form.addEventListener(type = "submit", callback = { event ->
        event.preventDefault()

        val message = formField.value
        ChatUser.Visitor.appendMessage(message)
        formField.value = ""

        window.setTimeout({
            GlobalScope.launch {
                ChatUser.Geoffrey.appendMessage(app.sendTestMessage(message))
            }
        }, 750)
    })
}

fun ChatUser.appendMessage(message: String) {
    val messageDiv: HTMLElement = document.create.div("message") {
        div("user-info") {
            img(classes = "person-img-one", src = url, alt = userName)
        }
        div("message-info") {
            div("person-name") {
                span("name") { +"$userName " }
                span("time") { +Date().toLocaleTimeString() }
            }
            div("person-text") {
                unsafe { +MarkdownIt().render(message) }
            }
        }
    }
    val messagesContainer = document.querySelector(".channel-messages")!!
    messagesContainer.insertBefore(messageDiv, messagesContainer.firstChild)
}

enum class ChatUser(
    val userName: String,
    val userHandle: String,
    val url: String
) {
    Geoffrey(
        "Geoffrey",
        "geoffrey",
        "http://localhost:8080/assets/geoffrey_100x100_tc.jpg"
    ),
    Sami(
        "Sami",
        "sami",
        "http://localhost:8080/assets/sami_100x100_tc.jpg"
    ),
    KyleD(
        "KyleD",
        "kyle",
        "http://localhost:8080/assets/kyle-d9_100x100_tc.jpg"
    ),
    Visitor(
        "You",
        "guest",
        "https://img.icons8.com/ios-filled/100/000000/user-male-circle.png"
    )
}

@JsModule("markdown-it")
@JsNonModule
external class MarkdownIt {
    fun render(input: String): String
}