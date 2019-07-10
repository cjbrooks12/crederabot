package com.caseyjbrooks.netlify.api.router.chat

open class ChatRouter : MessageHandler {

    internal val handlers: MutableList<MessageHandler> = mutableListOf()

    override suspend fun matches(from: String, message: String): Boolean {
        return handlers.any { it.matches(from, message) }
    }

    override suspend fun handle(context: ChatContext): ChatReply? {
        val apiHandler = handlers.firstOrNull { it.matches(context.from, context.originalMessage) }
        return apiHandler?.handle(context)
    }

    override fun getMessageFormats(): List<String> {
        return handlers.flatMap { it.getMessageFormats() }
    }

}
