package com.caseyjbrooks.netlify.handlers

import com.caseyjbrooks.netlify.app
import com.caseyjbrooks.netlify.router.Response
import com.caseyjbrooks.netlify.router.Router
import com.caseyjbrooks.netlify.router.before
import com.caseyjbrooks.netlify.tests.runTests

fun Router.testing() {

    before {
        if(app().env.runTests && !runTests()) {
            Pair(null, Response(500, "failed tests"))
        }
        else {
            Pair(null, null)
        }
    }

}