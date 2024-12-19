import React from 'react';
import { Link } from 'react-router-dom';

const Nav = ({ isAuthenticated, onLogout }) => {
    return (
        <nav className="navbar navbar-expand-lg navbar-dark bg-dark">
            <div className="container-fluid">
                <Link to="/" className="navbar-brand">Home</Link>
                <button
                    className="navbar-toggler collapsed"
                    type="button"
                    data-bs-toggle="collapse"
                    data-bs-target="#navbarSupportedContent"
                    aria-controls="navbarSupportedContent"
                    aria-expanded="false"
                    aria-label="Toggle navigation"
                >
                    <span className="navbar-toggler-icon"></span>
                </button>
                <div className="navbar-collapse collapse" id="navbarSupportedContent">
                    <ul className="navbar-nav me-auto mb-2 mb-lg-0">
                        <li className="nav-item">
                            <Link to="/blog" className="nav-link">Blog</Link>
                        </li>
                        <li className="nav-item">
                            <Link to="/photos" className="nav-link">Photography</Link>
                        </li>
                        <li className="nav-item">
                            <Link to="/wfl" className="nav-link">What's For Lunch</Link>
                        </li>
                    </ul>

                    {/* Different containers for Login/Sign-up and Logout */}
                    {!isAuthenticated ? (
                        <div className="d-lg-flex col-lg-3 justify-content-lg-end">
                            <Link to="/login" className="btn btn-outline-light me-2">
                                Login
                            </Link>
                            <Link to="/signup" className="btn btn-warning">
                                Sign-up
                            </Link>
                        </div>
                    ) : (
                        <div className="col-auto d-flex justify-content-end align-items-center">
                            <button
                                type="button"
                                className="btn btn-danger btn-md" // Use btn-md for medium size
                                onClick={onLogout}
                            >
                                Logout
                            </button>
                        </div>
                    )}
                </div>
            </div>
        </nav>
    );
};

export default Nav;
