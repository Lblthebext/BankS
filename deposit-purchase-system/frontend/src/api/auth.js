import http from './http';

export function fetchToken(userId = 10001) {
  return http.post('/auth/token', { userId });
}
