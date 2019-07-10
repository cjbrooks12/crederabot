package com.caseyjbrooks.netlify.api.router.chat

import kotlin.jvm.JvmName

@JvmName("replyr")
fun String.reply() = reply(this)

fun reply(message: String) = ChatReply(message)

