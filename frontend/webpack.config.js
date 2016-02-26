var webpack = require('webpack'),
    HtmlWebpackPlugin = require('html-webpack-plugin');

module.exports = {
    entry: {
        app: './app/client.entry.js'
    },
    output: {
        path: './build/',
        filename: '[name].bundle.js'
    },
    module: {
        loaders: [
            {
                test:   /\.scss$/,
                exclude: /(node_modules|bower_components)/,
                loader: 'style!css!autoprefixer!sass'
            },            {
                test: /\.js$/,
                exclude: /(node_modules|bower_components)/,
                loader: 'babel-loader'
            },
            {
                test: /\.html$/,
                exclude: /(node_modules|bower_components)/,
                loader: 'raw'
            }
        ]
    },
    plugins: [
        new HtmlWebpackPlugin({
            'filename': './index.html',
            'inject': true,
            'template': './app/index.html',
            'chunks': ['app']
        }),
        new webpack.NoErrorsPlugin()
    ]
};
