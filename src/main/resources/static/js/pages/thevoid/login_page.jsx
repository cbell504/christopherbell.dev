import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import LoginForm from '../components/void/forms/loginform.js'

class LoginPage extends Component {
    constructor(props) {
        super(props);
        this.state = {
            isAccountLoggedIn: localStorage.hasOwnProperty('voidLoginToken') && localStorage.hasOwnProperty("voidAccountId")
        };
    }

    render() {
        return (
            <div id="login-page-root">
                <LoginForm />
            </div>
        );
    }
}

export default LoginPage;