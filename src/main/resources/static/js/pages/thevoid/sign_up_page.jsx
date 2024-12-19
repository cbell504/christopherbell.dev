import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import SignUpForm from '../components/void/forms/signupform.js'

class SignUpPage extends Component {
    constructor(props) {
        super(props);
        this.state = {
            isAccountLoggedIn: localStorage.hasOwnProperty('voidLoginToken') && localStorage.hasOwnProperty("voidAccountId")
        };
    }

    render() {
        return (
            <div id="sign-up-page-root">
                <SignUpForm />
            </div>
        );
    }
}

export default SignUpPage;