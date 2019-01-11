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

import KotlinApp from "KotlinApp";

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

    // return error, event not supported
    return {statusCode: 404, body: "event not handled"};
};
