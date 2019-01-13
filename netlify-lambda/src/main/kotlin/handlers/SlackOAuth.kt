package com.caseyjbrooks.netlify.handlers

import com.caseyjbrooks.netlify.Response
import com.caseyjbrooks.netlify.Router
import com.caseyjbrooks.netlify.app
import com.caseyjbrooks.netlify.data.Fetch
import com.caseyjbrooks.netlify.data.fetchJsonNow
import com.caseyjbrooks.netlify.data.get
import com.caseyjbrooks.netlify.data.getFirebaseDatabase
import com.caseyjbrooks.netlify.data.setNow
import com.caseyjbrooks.netlify.router.get

fun Router.slackOAuthFlow() {

    get("slack-redirect") { body ->
        val env = app().env
        val url = "https://slack.com/api/oauth.access?" +
                "code=${body.query.code}" +
                "&client_id=${env.slackClientId}" +
                "&client_secret=${env.slackClientSecret}" +
                "&redirect_uri=${env.slackRedirectUrl}"

        val options: dynamic = object {}
        options["method"] = "GET"

        val jsonResponse: dynamic = Fetch.fetchJsonNow(url, options)
        console.log(jsonResponse)

        if (!jsonResponse.ok) {
            Response(200, "Error encountered: \n" + JSON.stringify(jsonResponse))
        } else {
            // save the token in the teams' database
            val teamSecureSettings = getFirebaseDatabase()["crederaPlusPlus"][jsonResponse.team_id]["secure"]

            val secureData: dynamic = object {}
            secureData["accessToken"] = jsonResponse.access_token
            secureData["botUserId"] = jsonResponse.bot.bot_user_id
            secureData["botAccessToken"] = jsonResponse.bot.bot_access_token

            teamSecureSettings.setNow(secureData)

            Response(200, "Success!")
        }
    }

}