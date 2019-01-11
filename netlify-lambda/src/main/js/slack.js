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

// import admin from "firebase-admin";
import KotlinApp from "KotlinApp";

// let firebase = admin;
//
// let serviceAccount = JSON.parse(process.env.FIREBASE_SERVICE_ACCOUNT_FILE);
//
// firebase.initializeApp({
//     credential: firebase.credential.cert(serviceAccount),
//     databaseURL: process.env.FIREBASE_URL
// });
//
// let db = firebase.database();

exports.handler = async (event, context) => {
    return KotlinApp.com.caseyjbrooks.netlify.app()
        .call(event.httpMethod, "/slack", JSON.parse(event.body))
        .then((response) => {
            console.log(response);
            return {
                statusCode: response.statusCode,
                body: JSON.stringify(response.body)
            }
        });

    // const match = body.event.text.match(messageRegex);
    // const userId = match[1];
    // const isPlus = match[2] === "++";
    // const reason = match[3].trim();
    //
    // return createOrUpdateRecord(userId, isPlus, reason)
    //     .then((newTotal) => {
    //         return getSlackUserInfo(userId, newTotal);
    //     })
    //     .then((response) => {
    //         return postMessageToSlack(response.displayName, body.event.channel, isPlus, response.newTotal, reason);
    //     })
    //
    //     // handle success and error
    //     .then(() => ({
    //         statusCode: 200,
    //         body: "success"
    //     }))
    //     .catch(error => ({
    //         statusCode: 422,
    //         body: `Oops! Something went wrong. ${error}`
    //     }));

    // return error, event not supported
    return {statusCode: 404, body: "event not handled"};
};

// function createOrUpdateRecord(userId, isPlus, reason) {
//     return new Promise(resolve => {
//         let ref = db.ref("crederaPlusPlus");
//         let usersRef = ref.child("users");
//
//         let user = usersRef.child(userId);
//
//         user.once("value", function (snapshot) {
//             let val = snapshot.val();
//
//             if (val) {
//                 user.update({
//                     score: val.score + ((isPlus) ? 1 : -1)
//                 });
//                 user.child("reasons").push({
//                     delta: ((isPlus) ? 1 : -1),
//                     reason: reason
//                 });
//             }
//             else {
//                 user.set({
//                     score: (isPlus) ? 1 : -1,
//                     reasons: []
//                 });
//                 user.child("reasons").push({
//                     delta: ((isPlus) ? 1 : -1),
//                     reason: reason
//                 });
//             }
//
//             user.child("score").once("value", function (updatedSnapshot) {
//                 resolve(updatedSnapshot.val());
//             });
//         });
//     });
// }
