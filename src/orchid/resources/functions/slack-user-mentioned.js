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
import admin from "firebase-admin";

let firebase = admin;

let serviceAccount = JSON.parse(process.env.FIREBASE_SERVICE_ACCOUNT_FILE);

firebase.initializeApp({
    credential: firebase.credential.cert(serviceAccount),
    databaseURL: process.env.FIREBASE_URL
});

let db = firebase.database();

exports.handler = async (event, context) => {
    // Only allow POST
    if (event.httpMethod !== "POST") {
        return {statusCode: 405, body: "Method Not Allowed"};
    }

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

                return createOrUpdateRecord(userId, isPlus, reason)
                    .then((newScore) => {
                        console.log(`updated score, now at ${newScore}. About to post to slack`);
                        return postMessageToSlack(userId, body.event.channel, isPlus, newScore, reason);
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

function createOrUpdateRecord(userId, isPlus, reason) {
    return new Promise(resolve => {
        let ref = db.ref("crederaPlusPlus");
        let usersRef = ref.child("users");

        let user = usersRef.child(userId);

        user.once("value", function (snapshot) {
            let val = snapshot.val();

            if (val) {
                console.log(`updating user ${userId}`);
                user.update({
                    score: val.score + ((isPlus) ? 1 : -1)
                });
                user.child("reasons").push({
                    delta: ((isPlus) ? 1 : -1),
                    reason: reason
                });
            }
            else {
                console.log(`creating new user ${userId}`);
                user.set({
                    score: (isPlus) ? 1 : -1,
                    reasons: []
                });
                user.child("reasons").push({
                    delta: ((isPlus) ? 1 : -1),
                    reason: reason
                });
            }

            user.child("score").once("value", function (updatedSnapshot) {
                console.log(`    user ${userId} now has ${updatedSnapshot.val()} points`);
                resolve(updatedSnapshot.val());
            });
        });
    });
}

function postMessageToSlack(userId, channel, isPlus, newTotal, reason) {
    console.log(`fetching profile info for user ${userId}`);
    return fetch(`https://slack.com/api/users.profile.get?token=${process.env.SLACK_TOKEN}&user=${userId}`, {
        method: "GET"
    }).then((response) => {
        console.log(`converting response to json`);
        return response.json();
    }).then((response) => {
        let message = `${response.profile.real_name_normalized} ${isPlus ? 'gains a point' : 'loses a point'} and now has ${newTotal} points ${(reason) ? `, ${isPlus ? '1' : '-1'} of which is for ${reason}` : ''}`;
        console.log(`posting message to slack ${userId}: ${message}`);
        return fetch("https://slack.com/api/chat.postMessage", {
            method: "POST",
            headers: {
                "content-type": "application/json",
                Authorization: `Bearer ${process.env.SLACK_TOKEN}`
            },
            body: JSON.stringify({
                channel: body.event.channel,
                text: message
            })
        });
    });
}