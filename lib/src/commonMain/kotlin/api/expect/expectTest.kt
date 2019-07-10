package com.caseyjbrooks.netlify.api.expect

import kotlinx.coroutines.CoroutineScope

expect fun runTest(block: suspend (scope: CoroutineScope) -> Unit)