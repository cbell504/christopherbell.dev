import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import HomeCryGallery from '../components/void/cries/homecrygallery.js'
import CryPoster from '../components/void/forms/cryposter.js'

class HomePage extends Component {
    constructor(props) {
        super(props);
        this.state = {
            isAccountLoggedIn: localStorage.hasOwnProperty('cbellLoginToken') && localStorage.hasOwnProperty("cbellEmail")
        };
    }

    render() {
        return (
            <div id="home-root">
                <div class="jumbotron jumbotron-fluid">
                    <div class="container-fluid">
                        <h1 class="display-3 text-center">Welcome</h1>
                    </div>
                </div>
                <div class="container-fluid bg-light py-5">
                    <div class="card-group">
                        <div class="card mb-4 text-center">
                            <div class="card-body">
                                <h5 class="card-title">Blog</h5>
                                <p class="card-text">Read my latest blog post</p>
                                <a href="/blog" class="btn btn-primary flush-light-blue">Go</a>
                            </div>
                        </div>
                        <div class="w-100 d-none d-sm-block d-md-none">
                        </div>
                        <div class="card mb-4 text-center">
                            <div class="card-body">
                                <h5 class="card-title">What's For Lunch?</h5>
                                <p class="card-text">Find out what to eat in Austin today.</p>
                                <a href="/wfl" class="btn btn-primary flush-light-blue">Go</a>
                            </div>
                        </div>
                        <div class="w-100 d-none d-md-block d-lg-none">
                        </div>
                        <div class="w-100 d-none d-sm-block d-md-none">
                        </div>
                        <div class="w-100 d-none d-lg-block d-xl-none">
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}

export default HomePage;