/**
 * PhotoGallery web component.
 *
 * Responsibilities:
 * - Fetch image metadata and render a responsive grid
 */
class PhotoGallery extends HTMLElement {
    constructor() {
        super();
        this.images = [];
        this.location = '/api/photos';
    }

    connectedCallback() {
        this.render();
        this.loadImages();
    }

    async loadImages() {
        try {
            const res = await fetch(this.location);
            const data = await res.json();
            this.images = data.images || [];
            this.update();
        } catch (e) {
            console.error('Failed to load gallery images', e);
        }
    }

    update() {
        const row = this.querySelector('.gallery-row');
        row.innerHTML = '';
        this.images.forEach(image => {
            const col = document.createElement('div');
            col.className = 'col';
            const img = document.createElement('img');
            img.src = image.path;
            img.className = 'img-fluid rounded';
            img.alt = '';
            col.appendChild(img);
            row.appendChild(col);
        });
    }

    render() {
        this.innerHTML = `
            <div class="container-fluid">
                <div class="row row-cols-1 row-cols-sm-1 row-cols-md-2 g-2 gallery-row"></div>
            </div>
        `;
    }
}

customElements.define('photo-gallery', PhotoGallery);
