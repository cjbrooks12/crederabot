package com.caseyjbrooks.netlify.router.chat

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
     * List the HTTP paths registered under this handler. Returns pairs of
     */
    fun getMessageFormats(): List<String>

}