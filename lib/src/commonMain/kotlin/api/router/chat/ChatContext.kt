package com.caseyjbrooks.netlify.api.router.chat

interface ChatContext {

    /**
     * The user who sent the message
     */
    val from: String

    /**
     * The full, unparsed message text received
     */
    val originalMessage: String

}