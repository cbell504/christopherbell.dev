/**
 * Login page behavior.
 *
 * Submits credentials to the login API, stores JWT on success, and
 * redirects to the home page. Redirects authenticated users away.
 */
import pubsub from '../components/pubsub.js';
import { API } from '../lib/api.js';

const alertBox = () => document.getElementById('loginAlert');

/**
 * Perform login against the API.
 * @param {string} email account email
 * @param {string} password account password
 * @returns {Promise<string>} JWT token
 */
async function login(email, password) {
  const resp = await fetch(API.accounts.login, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password })
  });
  const data = await resp.json().catch(() => ({}));
  if (!resp.ok || !data.success) {
    const msg = data?.messages?.[0]?.description || 'Login failed. Please try again.';
    throw new Error(msg);
  }
  return data.payload; // JWT token
}

/** Wire form submit and redirect rules once DOM is ready. */
document.addEventListener('DOMContentLoaded', () => {
  // If already logged in, redirect to home
  if (localStorage.getItem('cbellLoginToken')) {
    window.location.href = '/';
    return;
  }
  const form = document.getElementById('loginForm');
  if (!form) return;

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    const email = document.getElementById('email')?.value?.trim();
    const password = document.getElementById('password')?.value || '';
    const alert = alertBox();
    alert?.classList.add('d-none');
    try {
      const token = await login(email, password);
      localStorage.setItem('cbellLoginToken', token);
      pubsub.publish('auth:login');
      window.location.href = '/';
    } catch (err) {
      if (alert) {
        alert.textContent = err.message;
        alert.classList.remove('d-none');
      }
    }
  });
});
