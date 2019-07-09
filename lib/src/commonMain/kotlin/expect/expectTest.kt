package com.caseyjbrooks.netlify.expect

import kotlinx.coroutines.CoroutineScope

expect fun runTest(block: suspend (scope: CoroutineScope) -> Unit)