package com.caseyjbrooks.netlify.data

import kotlin.js.Promise

@JsModule("node-fetch")
@JsNonModule
external object Fetch {
    @JsName("default")
    fun fetch(url: String, data: dynamic) : Promise<dynamic>
}

