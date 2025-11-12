import http from './http';

export function submitPurchase(payload) {
  return http.post('/purchase', payload);
}
