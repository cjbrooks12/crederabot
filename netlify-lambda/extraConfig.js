const path = require('path');

module.exports = {
    resolve: {
        mainFields: [
            "main",
            "module"
        ],
        alias: {
            KotlinApp$: path.resolve(__dirname, 'build/classes/kotlin/main/netlify-lambda.js'),
        }
    }
};