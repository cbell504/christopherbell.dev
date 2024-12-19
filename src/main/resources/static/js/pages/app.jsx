import React, { Component } from 'react';
import { Routes, Route, Navigate } from "react-router-dom";
import BlogPage from './blog.jsx';
import HomePage from './home.jsx';
import Layout from './layout.jsx';
import LoginPage from './login.jsx';
import PhotosPage from './photos.jsx';
import NoMatch from "./no-match.jsx";
import SignUpPage from './signup.jsx';
import WhatsForLunchPage from './wfl.jsx';

// Protected Route Wrapper
const ProtectedRoute = ({ isAuthenticated, children }) => {
    if (!isAuthenticated) {
        // Redirect to login if not authenticated
        return <Navigate to="/login" />;
    }
    return children;
};

class App extends Component {
    constructor(props) {
        super(props);

        // Initialize state to track authentication
        this.state = {
            isAuthenticated: !!localStorage.getItem('cbellLoginToken'),
        };
    }

    handleLogin = (token) => {
        // Save the token in localStorage
        localStorage.setItem('cbellLoginToken', token);

        // Update the state
        this.setState({ isAuthenticated: true });
    };

    handleLogout = () => {
        // Remove the token from localStorage
        localStorage.removeItem('cbellLoginToken');

        // Update the state
        this.setState({ isAuthenticated: false });
    };

    render() {
        return (
            <div id="app-root">
                <Routes>
                    {/* Public routes */}
                    <Route path="/" element={<Layout isAuthenticated={this.state.isAuthenticated} onLogout={this.handleLogout} />}>
                        <Route index element={<HomePage />} />
                        <Route path="/login" element={<LoginPage onLogin={this.handleLogin} />} />
                        <Route path="/signup" element={<SignUpPage />} />

                        {/* Protected routes */}
                        <Route
                            path="/blog"
                            element={
                                <ProtectedRoute isAuthenticated={this.state.isAuthenticated}>
                                    <BlogPage />
                                </ProtectedRoute>
                            }
                        />
                        <Route
                            path="/photos"
                            element={
                                <ProtectedRoute isAuthenticated={this.state.isAuthenticated}>
                                    <PhotosPage />
                                </ProtectedRoute>
                            }
                        />
                        <Route
                            path="/wfl"
                            element={
                                <ProtectedRoute isAuthenticated={this.state.isAuthenticated}>
                                    <WhatsForLunchPage />
                                </ProtectedRoute>
                            }
                        />
                        {/* Catch-all route */}
                        <Route path="/*" element={<NoMatch />} />
                    </Route>
                </Routes>
            </div>
        );
    }
}

export default App;
