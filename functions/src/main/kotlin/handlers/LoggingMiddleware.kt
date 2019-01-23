package com.caseyjbrooks.netlify.handlers

import com.caseyjbrooks.netlify.app
import com.caseyjbrooks.netlify.router.Router
import com.caseyjbrooks.netlify.router.after
import com.caseyjbrooks.netlify.router.before

fun Router.logging() {

    before { req ->
        if(app().env.logRequests) {
            console.log("========================= CALLING ROUTE =========================")
            console.log("===== path: [${req.path}]")
            console.log("====================== REQUEST BODY START =======================")
            console.log(JSON.stringify(req.body, null, 2))
            console.log("======================= REQUEST BODY END ========================")
            console.log("========================= HANDLE BEGIN ==========================")
        }
        Pair(null, null)
    }

    after { _, resp ->
        if(app().env.logRequests) {
            console.log("========================== HANDLE END ===========================")
            console.log("====================== RESPONSE BODY START ======================")
            console.log(JSON.stringify(resp.body, null, 2))
            console.log("======================= RESPONSE BODY END =======================")
            console.log("=========================== ROUTE END ===========================")
            console.log()
            console.log()
        }
        resp
    }

}