package com.caseyjbrooks.netlify.impl

import com.caseyjbrooks.netlify.api.App
import com.caseyjbrooks.netlify.api.EnvVar
import com.caseyjbrooks.netlify.api.router.http.HttpRootRouter

class TestApp(
    override val env: EnvVar = DebugEnv(),
    override val router: HttpRootRouter = HttpRootRouter {}
) : App