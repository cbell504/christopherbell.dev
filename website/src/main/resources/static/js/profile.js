import { authHeaders, fetchJson, sanitize, isLoggedIn, formatWhen } from './lib/util.js';
import { API } from './lib/api.js';
import { createFeedItem } from './lib/feed-render.js';

/** Get the alert element for error display. */
const alertBox = () => document.getElementById('profileAlert');

/**
 * Render the account summary panel.
 * @param {{username:string,email:string,firstName?:string,lastName?:string,role?:string,status?:string}} detail
 */
function renderAccount(detail) {
  const root = document.getElementById('accountDetails');
  if (!root) return;
  const set = (name, value) => {
    const el = root.querySelector(`[data-field="${name}"]`);
    if (el) el.textContent = value ?? '—';
  };
  set('username', detail.username);
  set('email', detail.email);
  const fullName = [detail.firstName, detail.lastName].filter(Boolean).join(' ').trim();
  set('name', fullName || '—');
  set('role', detail.role);
  set('status', detail.status);
}

const ROOT_CACHE = {};

/**
 * Render the profile feed list.
 * @param {Array} posts feed items
 * @param {string} username current user's handle
 */
function renderPosts(posts, username) {
  const container = document.getElementById('postsList');
  if (!container) return;
  container.innerHTML = '';
  if (!posts || posts.length === 0) {
    container.innerHTML = '<div class="list-group-item">No posts yet.</div>';
    return;
  }
  for (const p of posts) {
    const canDelete = () => true; // current user owns their posts on /profile
    const fetchRootCached = async (rootId) => {
      if (!ROOT_CACHE[rootId]) ROOT_CACHE[rootId] = await fetchJson(API.posts.byId(rootId));
      return ROOT_CACHE[rootId];
    };
    const onLike = (postId) => fetchJson(API.posts.like(postId), { method: 'POST', headers: authHeaders() });
    const onDelete = (postId) => fetchJson(API.posts.byId(postId), { method: 'DELETE', headers: authHeaders() });
    const el = createFeedItem({ ...p, username }, { sanitize, formatWhen, isLoggedIn, canDelete, fetchRoot: fetchRootCached, fetchParent: fetchRootCached, onLike, onDelete });
    container.appendChild(el);
  }
}

/** Wire page once DOM is ready. */
document.addEventListener('DOMContentLoaded', async () => {
  if (!isLoggedIn()) {
    // Must be logged in
    window.location.href = '/login';
    return;
  }
  const alert = alertBox();
  alert?.classList.add('d-none');
  try {
    const me = await fetchJson(API.accounts.me, { headers: authHeaders() });
    renderAccount(me);
    const posts = await fetchJson(`${API.posts.meFeed}?limit=20`, { headers: authHeaders() });
    renderPosts(posts, me?.username);
/**
 * Profile page behavior.
 *
 * Responsibilities:
 * - Require authentication and load current account details
 * - Render user's feed with parent-context for replies
 * - Support liking and deleting (owner/admin) items
 */
  } catch (err) {
    if (alert) {
      alert.textContent = err.message;
      alert.classList.remove('d-none');
    }
  }
  // Close menus on outside click
  document.addEventListener('click', () => {
    document.querySelectorAll('.post-menu').forEach(m => m.classList.add('d-none'));
  });
});
