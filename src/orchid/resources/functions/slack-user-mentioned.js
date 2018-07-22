/*---
method: 'post'
summary: 'Dont be shy, say hello!'
parameters: []
responses:
  - status: '200'
    desc: A paged array of pets
  - status: 'default'
    desc: unexpected error
---*/

import fetch from "node-fetch";

exports.handler = async (event, context) => {
    // Only allow POST
    if (event.httpMethod !== "POST") {
        return {statusCode: 405, body: "Method Not Allowed"};
    }


    // When the method is POST, the name will no longer be in the event’s
    // queryStringParameters – it’ll be in the event body encoded as a queryString
    const body = JSON.parse(event.body);
    const challenge = body.challenge;

    // handle URL challenge
    if (body.type === "url_verification") {
        console.log("Handling URL Verification");
        return {statusCode: 200, body: challenge};
    }

    // handle Events API callback
    else if (body.type === "event_callback") {

        // handle message posted
        if(body.event.type === "message" && body.event.text.includes("\+\+")) {
            console.log("Handling messages.channel");
            // Send greeting to Slack
            return fetch(process.env.SLACK_WEBHOOK_URL,
                {
                    headers: {
                        "content-type": "application/json"
                    },
                    method: "POST",
                    body: JSON.stringify({text: `Slack says hello!`})
                })
                .then(() => ({
                    statusCode: 200,
                    body: "success"
                }))
                .catch(error => ({
                    statusCode: 422,
                    body: `Oops! Something went wrong. ${error}`
                }));
        }
        else {
            console.log(`Event callback type [${body.type}] not supported`);
            return {statusCode: 404, body: `Event callback type [${body.type}] not supported`};
        }
    }

    // return error, event not supported
    else {
        console.log(`Event type [${body.type}] not supported`);
        return {statusCode: 404, body: `Event type [${body.type}] not supported`};
    }
};