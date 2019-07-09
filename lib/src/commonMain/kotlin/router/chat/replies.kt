package com.caseyjbrooks.netlify.router.http

import com.caseyjbrooks.netlify.router.chat.ChatReply
import kotlin.jvm.JvmName

@JvmName("replyr")
fun String.reply() = reply(this)

fun reply(message: String) = ChatReply(message)

