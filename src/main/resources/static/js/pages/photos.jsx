import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import LoginForm from '../components/void/forms/loginform.js'

class PhotosPage extends Component {
    constructor(props) {
        super(props);
        this.state = {
            isAccountLoggedIn: localStorage.hasOwnProperty('cbellLoginToken') && localStorage.hasOwnProperty("cbellEmail")
        };
    }

    render() {
        return (
            <main>
                <section class="py-5 text-center main-color">
                    <h1 class="fw-light">Photo Gallery</h1>
                </section>
                <div class="bg-light py-5" id="gallery"></div>
            </main>
        );
    }
}

export default PhotosPage;