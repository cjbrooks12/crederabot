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

import functions from "functions";

exports.handler = async (event, context) => {
    let body;
    try {
        body = JSON.parse(event.body);
    }
    catch {
        body = {};
    }
    body.query = event.queryStringParameters;
    return functions.com.caseyjbrooks.netlify.app()
        .call(event.httpMethod, "/slack", body)
        .then((response) => {
            return {
                statusCode: response.statusCode,
                body: JSON.stringify(response.body)
            }
        });
};
