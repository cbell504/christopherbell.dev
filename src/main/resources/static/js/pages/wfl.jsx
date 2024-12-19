import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import LoginForm from '../components/void/forms/loginform.js'

class WhatsForLunchPage extends Component {
    constructor(props) {
        super(props);
        this.state = {
            isAccountLoggedIn: localStorage.hasOwnProperty('cbellLoginToken') && localStorage.hasOwnProperty("cbellEmail")
        };
    }

    render() {
        return (
            <main role="main">
                <div class="jumbotron jumbotron-fluid">
                    <div class="container">
                        <h1 class="display-3 text-center">What's For Lunch?</h1>
                        <div id="whats-for-lunch"></div>
                    </div>
                </div>
            </main>
        );
    }
}

export default WhatsForLunchPage;