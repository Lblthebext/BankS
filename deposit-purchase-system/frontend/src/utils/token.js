import { fetchToken } from '../api/auth';

const TOKEN_KEY = 'deposit-token';
let pendingPromise = null;

export function getToken() {
  return localStorage.getItem(TOKEN_KEY);
}

export async function ensureToken() {
  const existing = getToken();
  if (existing) {
    return existing;
  }
  if (!pendingPromise) {
    pendingPromise = fetchToken()
      .then(res => {
        const token = res?.data?.token;
        if (token) {
          localStorage.setItem(TOKEN_KEY, token);
        }
        return token;
      })
      .finally(() => {
        pendingPromise = null;
      });
  }
  return pendingPromise;
}

export function clearToken() {
  localStorage.removeItem(TOKEN_KEY);
}
