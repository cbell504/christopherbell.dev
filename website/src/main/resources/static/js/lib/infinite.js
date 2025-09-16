/**
 * Simple infinite scroll helper (window-based).
 *
 * Usage:
 *   const scroller = createInfiniteScroller({
 *     fetchPage: async ({ before, limit }) => [...items],
 *     onPage: (items) => { * append to DOM * },
 *     getCursor: (item) => item.createdOn || item.lastUpdatedOn,
 *     thresholdPx: 200,
 *     limit: 20,
 *   });
 *   scroller.loadInitial();
 */
export function createInfiniteScroller({ fetchPage, onPage, getCursor, thresholdPx = 200, limit = 20, onEmpty }) {
  let before = null;
  let loading = false;
  let done = false;

  const cursorFn = getCursor || ((it) => it.createdOn || it.lastUpdatedOn);

  async function load(renew = false) {
    if (loading || done) return;
    loading = true;
    try {
      const page = await fetchPage({ before: renew ? null : before, limit });
      if (!page || page.length === 0) {
        if (renew && typeof onEmpty === 'function') {
          onEmpty();
        }
        done = true;
        return;
      }
      onPage(page);
      const last = page[page.length - 1];
      before = cursorFn(last);
      if (page.length < limit) done = true;
    } finally {
      loading = false;
    }
  }

  function onScroll() {
    const nearBottom = window.innerHeight + window.scrollY >= document.body.offsetHeight - thresholdPx;
    if (nearBottom) load(false);
  }

  function loadInitial() {
    before = null;
    loading = false;
    done = false;
    load(true);
  }

  function attach() {
    window.addEventListener('scroll', onScroll);
  }

  function detach() {
    window.removeEventListener('scroll', onScroll);
  }

  return { loadInitial, attach, detach };
}
