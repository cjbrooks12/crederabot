package com.caseyjbrooks.netlify.router.chat

class ChatRootRouter(
    initializer: ChatRouter.() -> Unit
) : MessageHandler {

    private val subRouter = ChatRouter()

    init {
        subRouter.initializer()
    }

    override suspend fun matches(from: String, message: String): Boolean {
        return subRouter.matches(from, message)
    }

    override suspend fun handle(context: ChatContext): ChatReply? {
        return subRouter.handle(context)
    }

    override fun getMessageFormats(): List<String> {
        return subRouter.getMessageFormats()
    }
}