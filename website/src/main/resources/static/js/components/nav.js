/**
 * Navigation web component.
 *
 * Renders the responsive navbar, adapts to auth state, and provides
 * a minimal JS fallback for the mobile hamburger when Bootstrap JS
 * is not present on the page.
 */
import pubsub from './pubsub.js';

class AppNav extends HTMLElement {
    /** Lifecycle hook: mount component and subscribe to auth changes. */
    connectedCallback() {
        this.render();
        pubsub.subscribe('auth:login', () => this.render());
        pubsub.subscribe('auth:logout', () => this.render());
    }

    /** Render the navbar markup based on authentication state. */
    render() {
        const isAuthenticated = !!localStorage.getItem('cbellLoginToken');
        this.innerHTML = `
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container-fluid">
        <a href="/" class="navbar-brand">Home</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="navbar-collapse collapse" id="navbarSupportedContent">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                ${isAuthenticated ? `<li class=\"nav-item\"><a href=\"/profile\" class=\"nav-link\">Profile</a></li>` : ''}
                <li class="nav-item"><a href="/wfl" class="nav-link">What's For Lunch</a></li>
            </ul>
            ${!isAuthenticated ? `
            <div class="d-lg-flex col-lg-3 justify-content-lg-end">
                <a href="/login" class="btn btn-outline-light me-2">Login</a>
                <a href="/signup" class="btn btn-warning">Sign-up</a>
            </div>` : `
            <div class="col-auto d-flex justify-content-end align-items-center gap-2">
                <button id="logout" type="button" class="btn btn-danger btn-md">Logout</button>
            </div>`}
        </div>
    </div>
</nav>`;
        const logoutBtn = this.querySelector('#logout');
        if (logoutBtn) {
            logoutBtn.addEventListener('click', () => {
                pubsub.publish('auth:logout');
            });
        }

        // Mobile toggler: if Bootstrap JS is present, let it handle.
        // Otherwise, add a minimal vanilla fallback toggle.
        const toggler = this.querySelector('.navbar-toggler');
        const target = this.querySelector('#navbarSupportedContent');
        const hasBootstrap = typeof window !== 'undefined' && window.bootstrap && window.bootstrap.Collapse;
        if (toggler && target && !hasBootstrap) {
            toggler.addEventListener('click', (e) => {
                e.preventDefault();
                const isShown = target.classList.contains('show');
                if (isShown) {
                    target.classList.remove('show');
                    toggler.setAttribute('aria-expanded', 'false');
                } else {
                    target.classList.add('show');
                    toggler.setAttribute('aria-expanded', 'true');
                }
            });
        }
    }
}

customElements.define('app-nav', AppNav);
