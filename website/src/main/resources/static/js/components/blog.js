/**
 * BlogPosts web component.
 *
 * Responsibilities:
 * - Periodically fetch and render posts and tags
 * - Allow filtering posts by tag
 */
class BlogPosts extends HTMLElement {
    constructor() {
        super();
        this.posts = [];
        this.tags = [];
        this.postLocation = '/blog/post';
        this.tagLocation = '/blog/tag';
        this.intervalId = null;
    }

    connectedCallback() {
        this.render();
        this.loadData();
        this.intervalId = setInterval(() => this.loadData(), 10000);
    }

    disconnectedCallback() {
        if (this.intervalId) {
            clearInterval(this.intervalId);
        }
    }

    async loadData() {
        await Promise.all([this.loadPosts(), this.loadTags()]);
    }

    async loadPosts() {
        try {
            const res = await fetch(this.postLocation);
            const data = await res.json();
            const posts = (data.blogPostPayload || []).reverse();
            this.posts = posts;
            this.updatePosts();
        } catch (e) {
            console.error('Failed to load posts', e);
        }
    }

    async loadTags() {
        try {
            const res = await fetch(this.tagLocation);
            const data = await res.json();
            this.tags = data.blogTagPayload || [];
            this.updateTags();
        } catch (e) {
            console.error('Failed to load tags', e);
        }
    }

    setPostLocation(tag) {
        if (tag) {
            this.postLocation = `/blog/tag/${tag}`;
        } else {
            this.postLocation = '/blog/post';
        }
        this.loadPosts();
    }

    updateTags() {
        const tagContainer = this.querySelector('.blogTags-container');
        tagContainer.innerHTML = '';

        const allWrapper = document.createElement('div');
        allWrapper.className = 'blogTags';
        const allBtn = document.createElement('button');
        allBtn.className = 'btn btn-dark';
        allBtn.textContent = 'all';
        allBtn.addEventListener('click', () => this.setPostLocation(''));
        allWrapper.appendChild(allBtn);
        tagContainer.appendChild(allWrapper);

        this.tags.forEach(tag => {
            const wrapper = document.createElement('div');
            wrapper.className = 'blogTags';
            const btn = document.createElement('button');
            btn.className = 'btn btn-dark';
            btn.textContent = tag;
            btn.value = tag;
            btn.addEventListener('click', () => this.setPostLocation(tag));
            wrapper.appendChild(btn);
            tagContainer.appendChild(wrapper);
        });
    }

    updatePosts() {
        const postsContainer = this.querySelector('.blogPosts');
        postsContainer.innerHTML = '';

        this.posts.forEach(post => {
            const article = document.createElement('div');
            article.className = 'blogArticle';
            article.innerHTML = `
                <h2 class="text-center">${post.title}</h2>
                <h5 class="text-center">Author: ${post.author}</h5>
                <h5 class="text-center">${post.date}</h5>
                <hr/>
                <pre>${post.post}</pre>
            `;
            postsContainer.appendChild(article);
        });
    }

    render() {
        this.innerHTML = `
            <div class="blogTags-container"></div>
            <div class="blogPosts"></div>
        `;
    }
}

customElements.define('blog-posts', BlogPosts);
