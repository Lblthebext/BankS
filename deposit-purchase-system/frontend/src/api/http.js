import axios from 'axios';

const instance = axios.create({
  baseURL: '/api'
});

instance.interceptors.request.use(config => {
  const token = localStorage.getItem('deposit-token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  config.headers['X-Sign-Timestamp'] = `${Date.now()}`;
  config.headers['X-Sign-Nonce'] = Math.random().toString(36).substring(2, 8);
  config.headers['X-Signature'] = 'debug-sign';
  return config;
});

export default instance;
