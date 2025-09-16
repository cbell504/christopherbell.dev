/**
 * Initialize a simple post composer.
 *
 * @param {object} opts
 *  - selectors: { composer:'#composer', prompt:'#composerPrompt', textarea:'#postText', counter:'#charCount', button:'#postBtn', alert:'#homeAlert' }
 *  - maxLength: number (default 280)
 *  - isLoggedIn: ()=>boolean
 *  - onSubmit: (text:string)=>Promise<void>
 * @returns {{destroy:()=>void, reset:()=>void}}
 */
export function initComposer({ selectors, maxLength = 280, isLoggedIn, onSubmit }) {
  const composer = document.querySelector(selectors.composer);
  const prompt = document.querySelector(selectors.prompt);
  const textarea = document.querySelector(selectors.textarea);
  const counter = document.querySelector(selectors.counter);
  const button = document.querySelector(selectors.button);
  const alertEl = document.querySelector(selectors.alert);

  function toggle() {
    const auth = isLoggedIn();
    if (composer) composer.classList.toggle('d-none', !auth);
    if (prompt) prompt.classList.toggle('d-none', auth);
  }

  function updateCounter() {
    if (!counter || !textarea) return;
    const len = (textarea.value || '').length;
    counter.textContent = `${len} / ${maxLength}`;
  }

  async function handleSubmit() {
    if (alertEl) alertEl.classList.add('d-none');
    const text = (textarea?.value || '').trim();
    if (!text) return;
    if (text.length > maxLength) {
      if (alertEl) { alertEl.textContent = `Post text exceeds ${maxLength} characters.`; alertEl.classList.remove('d-none'); }
      return;
    }
    await onSubmit(text);
    reset();
  }

  function reset() {
    if (textarea) textarea.value = '';
    updateCounter();
  }

  // Wire up
  toggle();
  updateCounter();
  textarea?.addEventListener('input', updateCounter);
  button?.addEventListener('click', handleSubmit);

  return {
    destroy() {
      textarea?.removeEventListener('input', updateCounter);
      button?.removeEventListener('click', handleSubmit);
    },
    reset,
  };
}

