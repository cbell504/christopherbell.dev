import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import HomeCryGallery from '../components/void/cries/homecrygallery.js'
import CryPoster from '../components/void/forms/cryposter.js'

class HomePage extends Component {
    constructor(props) {
        super(props);
        this.state = {
            isAccountLoggedIn: localStorage.hasOwnProperty('voidLoginToken') && localStorage.hasOwnProperty("voidAccountId")
        };
    }

    render() {
        return (
            <div id="home-page-root">
                <CryPoster />
                <HomeCryGallery />
            </div>
        );
    }
}

export default HomePage;