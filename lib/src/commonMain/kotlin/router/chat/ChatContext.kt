package com.caseyjbrooks.netlify.router.chat

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