'use strict';
let path = require('path');
let defaultSettings = require('./defaults');

let additionalPaths = [];

module.exports = {
  additionalPaths: additionalPaths,
  port: defaultSettings.port,
  debug: true,
  devtool: 'eval',
  output: {
    path: path.join(__dirname, '/../dist/assets'),
    filename: 'app.js',
    publicPath: `.${defaultSettings.publicPath}`
  },
  devServer: {
    contentBase: './src/',
    historyApiFallback: true,
    hot: true,
    port: defaultSettings.port,
    publicPath: defaultSettings.publicPath,
    noInfo: false
  },
  resolve: {
    extensions: ['', '.js', '.jsx'],
    alias: {
      components: `${defaultSettings.srcPath}/components/`,
      config: `${defaultSettings.srcPath}/config/` + process.env.REACT_WEBPACK_ENV
    }
  },
  node: {
    net: 'empty'
  },
  module: {
    noParse: ['websocket'] //remove later for noParse + externals; acorn throwing a fit otherwise...go node...
  },
  externals: ['websocket']
};
