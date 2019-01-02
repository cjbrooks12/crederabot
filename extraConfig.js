const path = require('path');
const KotlinWebpackPlugin = require('@jetbrains/kotlin-webpack-plugin');

// module.exports = {
//     resolve: {
//         // "kotlin_build" is the build output directory
//         modules: ['kotlin_build', 'node_modules'],
//         mainFields: [
//             "main",
//             "module"
//         ]
//     },
//
//     // [OPTIONAL] To enable sourcemaps, source-map-loader should be configured
//     module: {
//         rules: [
//             {
//                 test: /\.js$/,
//                 include: path.resolve(__dirname, '../build/netlify/kotlin'),
//                 exclude: [
//                     /kotlin\.js$/, // Kotlin runtime doesn't have sourcemaps at the moment
//                 ],
//                 use: ['source-map-loader'],
//                 enforce: 'pre'
//             }
//         ]
//     },
//
//     output: {
//         path: __dirname + '/build/netlify/functions',
//         filename: 'index.js'
//     },
//
//     plugins: [
//         new KotlinWebpackPlugin({
//             src: __dirname + '/src/netlify/kotlin'
//         })
//     ]
// };




// module.exports = {
//     resolve: {
//         mainFields: [
//             "main",
//             "module"
//         ],
//         alias: {
//             KotlinApp$: path.resolve(__dirname, 'kotlin_build/kotlinApp.js'),
//         }
//     },
//     plugins: [
//         new KotlinWebpackPlugin({
//             src: __dirname + '/src/netlify/kotlin'
//         })
//     ]
// };



module.exports = {
    resolve: {
        mainFields: [
            "main",
            "module"
        ]
    }
};