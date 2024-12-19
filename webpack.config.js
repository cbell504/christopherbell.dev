// Imports
const path = require('path');

// Variables
const outputDirectory = './src/main/resources/static/js';

module.exports = {
  mode: 'development',
  entry: [
    /**
    * Add Bootstrap CSS and JS to the entry points, keeping it modular.
    */
    'bootstrap/dist/css/bootstrap.min.css', // Bootstrap CSS
    'bootstrap/dist/js/bootstrap.bundle.min.js', // Bootstrap JS (includes Popper.js)
    /**
     * The list for React components starts here. Please keep these in 
     * alphabetical order.
     */
//    './src/main/resources/static/js/components/blog/blog.js',
//    './src/main/resources/static/js/components/blogtags/blogtags.js',
//    './src/main/resources/static/js/components/footer/footer.js',
//    './src/main/resources/static/js/components/gallery/gallery.js',
//    './src/main/resources/static/js/components/nav/nav.js',
//    './src/main/resources/static/js/components/nav/navitems/navdropdown/navdropdown.js',
//    './src/main/resources/static/js/components/nav/navitems/navdropdown/navdropdownitem/navdropdownitem.js',
//    './src/main/resources/static/js/components/nav/navitems/navstditem/navstditem.js',
    './src/main/resources/static/js/pages/main.js',
  ],
  output: {
    path: path.join(__dirname, outputDirectory),
    filename: 'main.js'
  },
  module: {
    rules: [
      {
        test: /\.js$|jsx/,
        exclude: /node_modules/,
        use: {
          loader: 'babel-loader',
          options: {
            presets: [
              '@babel/preset-env',
              '@babel/preset-react'
            ],
            plugins: [
              '@babel/plugin-proposal-class-properties',
              '@babel/plugin-transform-runtime'  // For async/await support
            ]
          }
        }
      },
      {
        test: /\.css$/i,
        use: [
          'style-loader',
          'css-loader',
          'postcss-loader'  // Added PostCSS for CSS optimizations
        ]
      },
      {
        test: /\.(woff|woff2|eot|ttf|otf|svg)$/, // For font and SVG assets used by Bootstrap
        type: 'asset/resource', // Handles assets like fonts/images
      },
    ],
  },
  devServer: {
    port: 3000,
    open: true,
    proxy: {
      '/': 'http://localhost:8080'
    }
  },
  resolve: {
    extensions: ['.js', '.jsx']  // Automatically resolve these extensions
  },
  // For better debugging support (optional)
  devtool: 'source-map',  // Easier debugging in development
};
