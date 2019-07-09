package com.caseyjbrooks.netlify.router.chat

class ChatMessageHandler(
    private val messageRegex: Regex,
    val callback: suspend ChatContext.(MatchResult.Destructured) -> ChatReply
) : MessageHandler {


    override suspend fun matches(from: String, message: String): Boolean {
        return messageRegex.matches(message)
    }

    override suspend fun handle(context: ChatContext): ChatReply? {
        return context.callback(messageRegex.matchEntire(context.originalMessage)!!.destructured)
    }

    override fun getMessageFormats(): List<String> {
        return listOf(messageRegex.pattern)
    }
}

fun ChatRouter.message(messageRegex: Regex, callback: suspend ChatContext.(MatchResult.Destructured) -> ChatReply) {
    handlers.add(ChatMessageHandler(messageRegex, callback))
}