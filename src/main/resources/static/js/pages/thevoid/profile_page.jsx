import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import ProfileCryGallery from '../components/void/cries/profilecrygallery.js'

class ProfilePage extends Component {
    constructor(props) {
        super(props);
        this.state = {
            isAccountLoggedIn: localStorage.hasOwnProperty('voidLoginToken') && localStorage.hasOwnProperty("voidAccountId")
        };
    }

    render() {
        //TODO: Get the username from the url and pass it to the profile cry gallery. then use the profile cry gallery to make the call for data with the username
        return (
            <div id="profile-page-root">
                <ProfileCryGallery />
            </div>
        );
    }
}

export default ProfilePage;