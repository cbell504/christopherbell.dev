import { authHeaders, fetchJson, sanitize, isLoggedIn, formatWhen, closeOnOutside } from './lib/util.js';
import { API } from './lib/api.js';
import { createFeedItem } from './lib/feed-render.js';
import { canDeleteFor, makeRendererContext } from './lib/feed-context.js';
import { createInfiniteScroller } from './lib/infinite.js';
import { initComposer } from './lib/composer.js';
/**
 * Home feed page script.
 * - Renders global feed with infinite scroll and 15s polling
 * - Provides a top composer for authenticated users
 * - Wires item actions (reply/like/delete) via feed-render + feed-context
 */

/** Update compose character counter. */
function updateCounter(el, counter) {
  const len = (el.value || '').length;
  counter.textContent = `${len} / 280`;
}

/** Enable/disable composer controls. */
function setComposerEnabled(enabled) {
  const postBtn = document.getElementById('postBtn');
  const postText = document.getElementById('postText');
  if (postBtn) postBtn.disabled = !enabled;
  if (postText) postText.disabled = !enabled;
}

let USER_STATE = { id: null, role: null, username: null };
let LATEST_TS = null;
let SCROLLER = null;
let RENDER_CTX = null;

/** Submit a new top-level post from the composer. */
async function submitPost() {
  const postText = document.getElementById('postText');
  const alert = document.getElementById('homeAlert');
  if (alert) alert.classList.add('d-none');
  const text = (postText?.value || '').trim();
  if (!text) return;
  if (text.length > 280) {
    if (alert) {
      alert.textContent = 'Post text exceeds 280 characters.';
      alert.classList.remove('d-none');
    }
    return;
  }
  try {
    await fetchJson(API.posts.create, {
      method: 'POST',
      headers: authHeaders(),
      body: JSON.stringify({ text })
    });
    if (postText) postText.value = '';
    const counter = document.getElementById('charCount');
    if (counter && postText) updateCounter(postText, counter);
    await loadFeed();
  } catch (err) {
    if (alert) {
      alert.textContent = err.message;
      alert.classList.remove('d-none');
    }
  }
}

/** Wire page once DOM is ready. */
document.addEventListener('DOMContentLoaded', async () => {
  // Load current user to determine delete permissions before first render
  const token = localStorage.getItem('cbellLoginToken');
  if (token) {
    try {
      const me = await fetchJson(API.accounts.me, { headers: authHeaders() });
      USER_STATE = { id: me.id, role: me.role, username: me.username };
    } catch (_) { /* ignore */ }
  }
  // Close any open menus on outside click
  closeOnOutside('.post-menu');
  // Composer
  initComposer({
    selectors: {
      composer: '#composer',
      prompt: '#composerPrompt',
      textarea: '#postText',
      counter: '#charCount',
      button: '#postBtn',
      alert: '#homeAlert',
    },
    isLoggedIn,
    onSubmit: async (text) => {
      await fetchJson(API.posts.create, { method: 'POST', headers: authHeaders(), body: JSON.stringify({ text }) });
      const feedList = document.getElementById('feedList');
      if (feedList) feedList.innerHTML = '';
      LATEST_TS = null;
      SCROLLER?.loadInitial();
      window.scrollTo({ top: 0, behavior: 'smooth' });
    },
  });
  // Show new posts banner when available
  const banner = document.getElementById('newPostsBanner');
  const showBtn = document.getElementById('showNewPostsBtn');
  if (showBtn) {
    showBtn.addEventListener('click', async () => {
      if (banner) banner.classList.add('d-none');
      const feedList = document.getElementById('feedList');
      if (feedList) feedList.innerHTML = '';
      LATEST_TS = null;
      SCROLLER?.loadInitial();
      window.scrollTo({ top: 0, behavior: 'smooth' });
    });
  }
  const feedList = document.getElementById('feedList');
  if (feedList) feedList.innerHTML = '';
  RENDER_CTX = makeRendererContext({ fetchJson, authHeaders, sanitize, formatWhen, isLoggedIn, canDelete: canDeleteFor(USER_STATE), currentUserName: USER_STATE?.username || null });
  SCROLLER = createInfiniteScroller({
    thresholdPx: 200,
    limit: 20,
    fetchPage: async ({ before, limit }) => {
      const params = new URLSearchParams();
      params.set('limit', String(limit));
      if (before) params.set('before', before);
      return await fetchJson(`${API.posts.feed}?${params.toString()}`, { headers: authHeaders() });
    },
    onPage: (items) => {
      if (!items || items.length === 0) return;
      if (!LATEST_TS) {
        const first = items[0];
        LATEST_TS = first.createdOn || first.lastUpdatedOn;
      }
      for (const p of items) feedList.appendChild(createFeedItem(p, RENDER_CTX));
    },
    getCursor: (it) => it.createdOn || it.lastUpdatedOn,
    onEmpty: () => { if (feedList) feedList.innerHTML = '<div class="list-group-item">No posts yet.</div>'; }
  });
  SCROLLER.attach();
  SCROLLER.loadInitial();
  // Poll for new posts every 15 seconds
  setInterval(async () => {
    try {
      const latest = await fetchJson('/api/posts/2025-09-14/feed?limit=1', { headers: authHeaders() });
      const top = latest && latest[0];
/**
 * Global home feed behavior.
 *
 * Responsibilities:
 * - Load newest posts with infinite scroll and 15s polling
 * - Render each item with user handle, content, parent-context (when reply)
 * - Allow posting (when authenticated), liking, and deleting (owner/admin)
 */
      if (!top) return;
      const topTs = top.createdOn || top.lastUpdatedOn;
      if (LATEST_TS && new Date(topTs) > new Date(LATEST_TS)) {
        if (banner) banner.classList.remove('d-none');
      }
    } catch (_) { /* ignore polling failures */ }
  }, 15000);
});
