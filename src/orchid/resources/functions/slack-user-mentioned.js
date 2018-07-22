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
            body: challenge
        }))
        .catch(error => ({
            statusCode: 422,
            body: `Oops! Something went wrong. ${error}`
        }));
};