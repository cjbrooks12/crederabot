/*---
method: 'post'
summary: 'Calls any url in the site, where body.path is the URL path to call'
parameters: []
responses:
  - status: '200'
    desc: A list of routes
---*/

import KotlinApp from "KotlinApp";

exports.handler = async (event, context) => {
    let body = JSON.parse(event.body);
    return KotlinApp.com.caseyjbrooks.netlify.app().call(event.httpMethod, body.path, body);
};
