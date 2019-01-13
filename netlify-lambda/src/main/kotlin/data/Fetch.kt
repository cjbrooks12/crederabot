package com.caseyjbrooks.netlify.data

import kotlinx.coroutines.await
import kotlin.js.Promise

@JsModule("node-fetch")
@JsNonModule
external object Fetch {
    @JsName("default")
    fun fetch(url: String, data: dynamic) : Promise<dynamic>
}

fun Fetch.fetchJson(url: String, data: dynamic) : Promise<dynamic> = fetch(url, data).then<dynamic> { it.json() }
suspend fun Fetch.fetchJsonNow(url: String, data: dynamic) : dynamic = fetchJson(url, data).await()