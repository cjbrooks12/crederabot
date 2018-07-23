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
            const messageRegex = /<@(\w+)>\s*?(\+\+|--)(.*)/;

            if (messageRegex.test(body.event.text)) {
                const match = body.event.text.match(messageRegex);
                const userId = match[1];
                const isPlus = match[2] === "++";
                const reason = match[3];

                return getOrCreateRecordInDatabase(userId)

                    .then((record) => {
                        return updateRecordToDatabase(userId, record.id, record.score, isPlus)
                    })
                    .then(() => {
                        return getUserProfileInfo(userId)
                    })
                    .then((data) => {
                        return postMessageToSlack(data.profile.real_name_normalized, isPlus, 1, reason);
                    })

                    // handle success and error
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

function getUserProfileInfo(userId) {
    return fetch(`https://slack.com/api/users.profile.get?token=${process.env.SLACK_TOKEN}&user=${userId}`, {
        method: "GET"
    }).then((response) => {
        return response.json();
    });
}

function postMessageToSlack(username, isPlus, newTotal, reason) {
    return fetch("https://slack.com/api/chat.postMessage", {
        method: "POST",
        headers: {
            "content-type": "application/json",
            Authorization: `Bearer ${process.env.SLACK_TOKEN}`
        },
        body: JSON.stringify({
            channel: body.event.channel,
            text: `${username} ${isPlus ? 'gains a point' : 'loses a point'} ${(reason) ? `for ${reason}` : ''}`
        })
    });
}

function getOrCreateRecordInDatabase(userId) {
    return fetch(`https://api.airtable.com/v0/${process.env.AIRTABLE_TABLE_ID}?filterByFormula={userid}='${userId}'`, {
        method: "GET",
        headers: {
            Authorization: `Bearer ${process.env.AIRTABLE_TOKEN}`
        }
    }).then((response) => {
        return response.json();
    }).then((data) => {
            if (data.records && data.records.length === 0) {
                return data.records[0];
            }
            else {
                return fetch(`https://api.airtable.com/v0/${process.env.AIRTABLE_TABLE_ID}`, {
                    method: "POST",
                    headers: {
                        Authorization: `Bearer ${process.env.AIRTABLE_TOKEN}`
                    },
                    body: JSON.stringify({
                        fields: {
                            userid: userId,
                            score: 0
                        }
                    })
                }).then((response) => {
                    return response.json();
                }).then((data) => {
                    return data.records[0];
                })
            }
        }
    );
}

function updateRecordToDatabase(userId, recordId, recordTotal, isPlus) {
    return fetch(`https://api.airtable.com/v0/${process.env.AIRTABLE_TABLE_ID}/${recordId}`, {
        method: "PUT",
        headers: {
            Authorization: `Bearer ${process.env.AIRTABLE_TOKEN}`
        },
        body: JSON.stringify({
            fields: {
                userid: userId,
                score: (isPlus) ? recordTotal + 1 : recordTotal - 1
            }
        })
    })
}

function getNewUserTotal(userId) {
    fetch(`https://api.airtable.com/v0/${process.env.AIRTABLE_TABLE_ID}?filterByFormula={userid}='${userId}'`, {
        method: "GET",
        headers: {
            Authorization: `Bearer ${process.env.AIRTABLE_TOKEN}`
        }
    }).then((response) => {
        return response.json();
    }).then((data) => {
        return data.records[0].fields.score
    });
}