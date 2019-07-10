package com.caseyjbrooks.netlify.api.handlers

import com.caseyjbrooks.netlify.api.Clog
import com.caseyjbrooks.netlify.api.router.http.HttpRouter
import com.caseyjbrooks.netlify.api.router.http.after
import com.caseyjbrooks.netlify.api.router.http.before

fun HttpRouter.logging() {

    before { req ->
        if (req.app.env.LOG_REQUESTS) {
            Clog.v("========================= CALLING ROUTE =========================")
            Clog.v("===== path: [${req.path}]")
            Clog.v("====================== REQUEST BODY START =======================")
            Clog.v("" + req.body)
            Clog.v("======================= REQUEST BODY END ========================")
            Clog.v("========================= HANDLE BEGIN ==========================")
        }
        Pair(null, null)
    }

    after { req, resp ->
        if (req.app.env.LOG_REQUESTS) {
            Clog.v("========================== HANDLE END ===========================")
            Clog.v("====================== RESPONSE BODY START ======================")
            Clog.v("" + resp.body)
            Clog.v("======================= RESPONSE BODY END =======================")
            Clog.v("=========================== ROUTE END ===========================")
        }
        resp
    }

}