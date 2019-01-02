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
// import FunctionHandler from "KotlinApp";

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

    // return {statusCode: 200, body: new FunctionHandler(event.httpMethod, "").handle()};

    //
    // const body = JSON.parse(event.body);
    // const challenge = body.challenge;
    //
    // // handle URL challenge
    // if (body.type === "url_verification") {
    //     return {statusCode: 200, body: challenge};
    // }
    //
    // // handle Events API callback
    // else if (body.type === "event_callback") {
    //     if (body.event.type === "message") {
    //         const messageRegex = /<@(\w+)>\s*?(\+\+|--)(.*)/;
    //
    //         if (messageRegex.test(body.event.text)) {
    //             const match = body.event.text.match(messageRegex);
    //             const userId = match[1];
    //             const isPlus = match[2] === "++";
    //             const reason = match[3].trim();
    //
    //             return createOrUpdateRecord(userId, isPlus, reason)
    //                 .then((newTotal) => {
    //                     return getSlackUserInfo(userId, newTotal);
    //                 })
    //                 .then((response) => {
    //                     return postMessageToSlack(response.displayName, body.event.channel, isPlus, response.newTotal, reason);
    //                 })
    //
    //                 // handle success and error
    //                 .then(() => ({
    //                     statusCode: 200,
    //                     body: "success"
    //                 }))
    //                 .catch(error => ({
    //                     statusCode: 422,
    //                     body: `Oops! Something went wrong. ${error}`
    //                 }));
    //         }
    //     }
    // }

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
                user.update({
                    score: val.score + ((isPlus) ? 1 : -1)
                });
                user.child("reasons").push({
                    delta: ((isPlus) ? 1 : -1),
                    reason: reason
                });
            }
            else {
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
                resolve(updatedSnapshot.val());
            });
        });
    });
}

function getSlackUserInfo(userId, newTotal) {
    return new Promise(resolve => {
        return fetch(`https://slack.com/api/users.profile.get?token=${process.env.SLACK_TOKEN}&user=${userId}`, {
            method: "GET"
        }).then((response) => {
            return response.json();
        }).then((response) => {
            resolve({
                displayName: response.profile.real_name_normalized,
                newTotal: newTotal
            });
        });
    });
}

function postMessageToSlack(userName, channel, isPlus, newTotal, reason) {
    return new Promise(resolve => {
        fetch("https://slack.com/api/chat.postMessage", {
            method: "POST",
            headers: {
                "content-type": "application/json",
                Authorization: `Bearer ${process.env.SLACK_TOKEN}`
            },
            body: JSON.stringify({
                channel: channel,
                text: `${userName} ${isPlus ? 'gains a point' : 'loses a point'} and now has ${newTotal} points${(reason) ? `, ${isPlus ? '1' : '-1'} of which is for ${reason}` : ''}`
            })
        }).then(() => {
            resolve("success");
        });
    });
}