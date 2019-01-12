/*---
method: 'post'
summary: 'Endpoint to handle Slack Events API.'
parameters: []
responses:
  - status: '200'
    desc: A paged array of pets
  - status: 'default'
    desc: unexpected error
---*/

import KotlinApp from "KotlinApp";

exports.handler = async (event, context) => {
    console.log(JSON.parse(event.body));
    return KotlinApp.com.caseyjbrooks.netlify.app()
        .call(event.httpMethod, "/slack", JSON.parse(event.body))
        .then((response) => {
            console.log(response);
            return {
                statusCode: response.statusCode,
                body: JSON.stringify(response.body)
            }
        });
};