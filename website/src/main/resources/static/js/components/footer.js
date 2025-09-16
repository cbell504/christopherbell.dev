/**
 * Footer web component.
 *
 * Renders a simple, neutral footer with the current year.
 */
class AppFooter extends HTMLElement {
    /** Lifecycle hook: mount component and inject markup. */
    connectedCallback() {
        const year = new Date().getFullYear();
        this.innerHTML = `
<footer class="py-3 mt-4">
    <div class="container text-center">
        <span class="text-muted">&copy; ${year} Christopher Bell</span>
    </div>
</footer>`;
    }
}

customElements.define('app-footer', AppFooter);
