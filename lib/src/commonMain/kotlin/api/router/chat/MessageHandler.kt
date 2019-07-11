package com.caseyjbrooks.netlify.api.router.chat

interface MessageHandler {

    /**
     * Check if any of this handler or any of its children can handle the request
     */
    suspend fun matches(from: String, message: String): Boolean

    /**
     * Given that this handler or one of its children can handle the request, handle it now. Returns true is the message
     * was handled properly.
     */
    suspend fun handle(context: ChatContext) : ChatReply?

    /**
     * List the message formats registered under this handler. Returns pairs of a human-readable message format to a
     * description of what that message format does
     */
    fun getMessageFormats(): List<Pair<String, String>>

    /**
     * Implements a Visitor pattern to explore all registered MessageHandlers
     */
    fun visit(onVisit: (MessageHandler) -> Unit)

}