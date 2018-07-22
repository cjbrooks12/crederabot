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
        return {statusCode: 200, body: challenge};
    }

    // handle Events API callback
    else if (body.type === "event_callback") {
        if (body.event.type === "message") {
            const messageRegex = /<@(\w+)>\s*?(\+\+|--)/;

            if (messageRegex.test(body.event.text)) {
                const match = messageRegex.exec(body.event.text);
                const userId = match[1];
                const isPlus = match[2] === "++";

                console.log(`Handling messages.channel ++ message: ${body.event.text} - ${userId} ${isPlus ? 'gains a point' : 'loses a point'}`);
                return fetch(process.env.SLACK_WEBHOOK_URL,
                    {
                        headers: {
                            "content-type": "application/json"
                        },
                        method: "POST",
                        body: JSON.stringify({text: `${userId} ${isPlus ? 'gains a point' : 'loses a point'}`})
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
        }
    }

    // return error, event not supported
    return {statusCode: 404, body: "event not handled"};
};