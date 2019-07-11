package com.caseyjbrooks.netlify.api.router.chat

class ChatMessageHandler(
    private val messageRegex: Regex,
    val callback: suspend ChatContext.(List<String>) -> ChatReply
) : MessageHandler {

    var simpleMessageFormat: String = messageRegex.pattern
    var simpleMessageDescription: String = ""

    override suspend fun matches(from: String, message: String): Boolean {
        return messageRegex.matches(message)
    }

    override suspend fun handle(context: ChatContext): ChatReply? {
        return context.callback(
            messageRegex.matchEntire(context.originalMessage)!!
                .destructured
                .toList()
                .map { it.trim() }
        )
    }

    override fun getMessageFormats(): List<Pair<String, String>> {
        return listOf(simpleMessageFormat to simpleMessageDescription)
    }

    override fun visit(onVisit: (MessageHandler) -> Unit) {
        onVisit(this)
    }
}

fun ChatRouter.message(messageRegex: Regex, configure: (ChatMessageHandler.()->Unit)? = null, callback: suspend ChatContext.(List<String>) -> ChatReply) {
    handlers.add(ChatMessageHandler(messageRegex, callback).apply { configure?.invoke(this) })
}