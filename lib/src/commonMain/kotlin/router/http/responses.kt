package com.caseyjbrooks.netlify.router.http

import kotlin.jvm.JvmName

@JvmName("okr")
fun Any?.ok() = ok(this)

fun ok(body: Any? = "") = Response(200, body)

@JvmName("notFoundr")
fun Any?.notFound() = notFound(this)

fun notFound(body: Any? = "") = Response(404, body)
