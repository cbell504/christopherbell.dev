import { sanitize, authHeaders, fetchJson, isLoggedIn, formatWhen, closeOnOutside } from './lib/util.js';
import { API } from './lib/api.js';
import { createFeedItem } from './lib/feed-render.js';
import { makeRendererContext, canDeleteFor } from './lib/feed-context.js';
/** Extract the post id from the /p/{id} path. */
function getPostId() { const m = location.pathname.match(/\/p\/(.+)$/); return m ? decodeURIComponent(m[1]) : null; }


/**
 * Render the root post (with context cards) using the shared feed renderer.
 * @param {object} post root post feed item
 * @param {{id?:string,role?:string,username?:string}|null} currentUser
 */
function renderRoot(post, currentUser) {
  const root = document.querySelector('#rootPost .card-body');
  if (!root) return;
  // Only render context cards here; the main post body comes from createFeedItem
  root.innerHTML = `
    ${(post.rootId && post.rootId !== post.id && post.rootId !== post.parentId) ? `
    <div class="root-context card mb-2" data-root="${post.rootId}">
      <div class="card-body py-2">
        <div class="small text-muted">Thread root</div>
        <div class="fw-semibold">
          <a href="/u/" class="link-underline link-underline-opacity-0" data-root-handle="${post.rootId}">@user</a>
        </div>
        <p class="mb-0 small fw-semibold" data-root-text="${post.rootId}">Loading…</p>
        <a href="/p/${encodeURIComponent(post.rootId)}" class="small">View root</a>
      </div>
    </div>` : ''}
    ${post.parentId ? `
    <div class="parent-context card mb-2" data-parent="${post.parentId}">
      <div class="card-body py-2">
        <div class="small text-muted">In reply to</div>
        <div class="fw-semibold">
          <a href="/u/" class="link-underline link-underline-opacity-0" data-parent-handle="${post.parentId}">@user</a>
        </div>
        <p class="mb-0 small fw-semibold" data-parent-text="${post.parentId}">Loading…</p>
        <a href="/p/${encodeURIComponent(post.parentId)}" class="small">View parent</a>
      </div>
    </div>` : ''}
  `;
  // Append the main post via shared feed renderer
  const ctx = makeRendererContext({ fetchJson, authHeaders, sanitize, formatWhen, isLoggedIn, canDelete: canDeleteFor(currentUser), currentUserName: currentUser?.username || null, suppressParentContext: true });
  root.appendChild(createFeedItem(post, ctx));

  // Fill parent context details if this is a reply
  if (post.parentId) {
    (async () => {
      try {
        const parent = await fetchJson(API.posts.byId(post.parentId), { headers: authHeaders() });
        const h = parent.username ? `@${sanitize(parent.username)}` : '@user';
        const handleEl = root.querySelector(`[data-parent-handle="${post.parentId}"]`);
        const textEl = root.querySelector(`[data-parent-text="${post.parentId}"]`);
        if (handleEl) {
          handleEl.textContent = h;
          handleEl.setAttribute('href', `/u/${encodeURIComponent(parent.username || '')}`);
        }
        if (textEl) {
          textEl.textContent = parent.text || '';
        }
      } catch (e) {
        const textEl = root.querySelector(`[data-parent-text="${post.parentId}"]`);
        if (textEl) textEl.textContent = 'Context unavailable';
      }
    })();
  }

  // Fill root context if different from parent
  if (post.rootId && post.rootId !== post.id && post.rootId !== post.parentId) {
    (async () => {
      try {
        const r = await fetchJson(API.posts.byId(post.rootId), { headers: authHeaders() });
        const h = r.username ? `@${sanitize(r.username)}` : '@user';
        const handleEl = root.querySelector(`[data-root-handle="${post.rootId}"]`);
        const textEl = root.querySelector(`[data-root-text="${post.rootId}"]`);
        if (handleEl) {
          handleEl.textContent = h;
          handleEl.setAttribute('href', `/u/${encodeURIComponent(r.username || '')}`);
        }
        if (textEl) {
          textEl.textContent = r.text || '';
        }
      } catch (e) {
        const textEl = root.querySelector(`[data-root-text="${post.rootId}"]`);
        if (textEl) textEl.textContent = 'Context unavailable';
      }
    })();
  }
}

/**
 * Render the replies list, excluding the currentId item if present.
 * @param {Array} items thread feed items
 * @param {{id?:string,role?:string}} currentUser current viewer (optional)
 * @param {string} currentId post id to omit from replies
 */
function renderThread(items, currentUser, currentId) {
  const list = document.getElementById('threadList');
  if (!list) return;
  list.innerHTML = '';
  const directReplies = (items || []).filter(p => p.parentId === currentId);
  const ctx = makeRendererContext({ fetchJson, authHeaders, sanitize, formatWhen, isLoggedIn, canDelete: canDeleteFor(currentUser), currentUserName: currentUser?.username || null, suppressParentContext: true });
  for (const p of directReplies) list.appendChild(createFeedItem(p, ctx));
}

/** Wire page once DOM is ready. */
document.addEventListener('DOMContentLoaded', async () => {
  closeOnOutside('.post-menu');
  const id = getPostId();
  if (!id) return;
  const alert = document.getElementById('postAlert');
  try {
    const [post, thread] = await Promise.all([
      fetchJson(API.posts.byId(id), { headers: authHeaders() }),
      fetchJson(API.posts.thread(id), { headers: authHeaders() })
    ]);
    let me = null;
    if (localStorage.getItem('cbellLoginToken')) {
      try { me = await fetchJson(API.accounts.me, { headers: authHeaders() }); } catch (_) {}
    }
    renderRoot(post, me);
    renderThread(thread, me, id);
    // The inline reply composer is available under the root; no need to show the top card.
    // If desired, we could still expose #replyComposer; keeping it hidden for a cleaner UI.
  } catch (err) {
    if (alert) { alert.textContent = err.message; alert.classList.remove('d-none'); }
  }
});
