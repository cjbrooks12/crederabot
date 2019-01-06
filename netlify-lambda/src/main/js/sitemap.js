/*---
method: 'get'
summary: 'Gets a list of all routes in the app'
parameters: []
responses:
  - status: '200'
    desc: A list of routes
---*/

import KotlinApp from "KotlinApp";

exports.handler = async (event, context) => {
    return KotlinApp.com.caseyjbrooks.netlify.app().call(event.httpMethod, "/sitemap", {});
};