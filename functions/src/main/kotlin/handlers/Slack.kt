package com.caseyjbrooks.netlify.handlers

import com.caseyjbrooks.netlify.SLACK_URL_VERIFICATION_TYPE
import com.caseyjbrooks.netlify.SLACK_WEBHOOK_PATH
import com.caseyjbrooks.netlify.app
import com.caseyjbrooks.netlify.data.Fetch
import com.caseyjbrooks.netlify.data.debounceSlackMessage
import com.caseyjbrooks.netlify.data.fetchJsonNow
import com.caseyjbrooks.netlify.data.get
import com.caseyjbrooks.netlify.data.getFirebaseDatabase
import com.caseyjbrooks.netlify.data.setNow
import com.caseyjbrooks.netlify.router.Response
import com.caseyjbrooks.netlify.router.Router
import com.caseyjbrooks.netlify.router.before
import com.caseyjbrooks.netlify.router.get
import com.caseyjbrooks.netlify.router.slack

fun Router.slackSetup() {

    // debounce message requests
    before { req ->
        val teamId = req.body?.team_id
        val channelId = req.body?.event?.channel
        val messageId = req.body?.event?.client_msg_id

        if(teamId != null && channelId != null && messageId != null) {
            val shouldContinue = debounceSlackMessage(teamId, messageId)
            if(shouldContinue) {
                Pair(req, null)
            }
            else {
                Pair(null, Response(200, "Already been handled :)"))
            }
        }
        else {
            // we don't even have a Slack message here
            Pair(req, null)
        }
    }

    // handle Slack URL verification
    slack(SLACK_URL_VERIFICATION_TYPE) { body -> Response(200, body.challenge) }

    // handle OAuth flow for registering other apps
    get(SLACK_WEBHOOK_PATH) { request ->
        console.log(request.body.query.code)

        val env = app().env
        val url = "https://slack.com/api/oauth.access?" +
                "code=${request.body.query.code}" +
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
            secureData["authorizedBy"] = jsonResponse.user_id
            secureData["botUserId"] = jsonResponse.bot.bot_user_id
            secureData["botAccessToken"] = jsonResponse.bot.bot_access_token

            teamSecureSettings.setNow(secureData)

            Response(200, "Success!")
        }
    }

}