import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import CryGallery from '../components/void/cries/crygallery.js'
import CryPoster from '../components/void/forms/cryposter.js'

class Home extends Component {
  render() {
    return (
        <div id="home">
          <CryPoster />
          <CryGallery />
        </div>
    );
  }
}

export default Home;