import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import { Link } from 'react-router-dom';

class NoMatch extends Component {
    constructor(props) {
        super(props);
        this.state = {
            isAccountLoggedIn: localStorage.hasOwnProperty('voidLoginToken') && localStorage.hasOwnProperty("voidAccountId")
        };
    }

    render() {
        return (
            <div>
                <h2>Nothing to see here!</h2>
                <p>
                    <Link to="/void">Go to the home page of the app</Link>
                </p>
            </div>
        );
    }
}

export default NoMatch;