var webpack = require('webpack');

module.exports = {
  context: __dirname,
  entry: {
    app: "./services/bootstrapService.js",
  },
  output: {
    path: __dirname + "/dist",
    filename: "[name].bundle.js",
  },
  module: {
    rules: [
      {
        test: /\.js$/,
        loader: 'babel-loader',
        exclude: /node_modules/,
        use: ['babel-loader'],
        query: {
          presets: [['es2015']]
        }
      },
    ]
  },
  resolve: {
    extensions: ['', '.js']
  }
};
