import React, { Component } from 'react';
import { Outlet } from 'react-router-dom';
import Nav from "../components/nav/nav.js";
import Footer from "../components/footer/footer.js";

class Layout extends Component {
    render() {
        const { isAuthenticated, onLogout } = this.props;

        return (
            <div id="layout-page-root">
                <Nav
                    isAuthenticated={isAuthenticated}
                    onLogout={onLogout}
                />
                <Outlet />
                <Footer />
            </div>
        );
    }
}

export default Layout;
