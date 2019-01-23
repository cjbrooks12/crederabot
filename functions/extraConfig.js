const path = require('path');

module.exports = {
    resolve: {
        mainFields: [
            "main",
            "module"
        ],
        modules: [
            path.resolve(__dirname, 'build/classes/kotlin/vendor/'),
            'node_modules'
        ]
    }
};