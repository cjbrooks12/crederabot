package com.caseyjbrooks.netlify.api.router.chat

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

    override fun getMessageFormats(): List<Pair<String, String>> {
        return subRouter.getMessageFormats()
    }

    override fun visit(onVisit: (MessageHandler) -> Unit) {
        onVisit(this)
        subRouter.visit(onVisit)
    }
}