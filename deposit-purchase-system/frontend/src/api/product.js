import http from './http';

export function getProducts() {
  return http.get('/products');
}

export function getProductDetail(id) {
  return http.get(`/products/${id}`);
}
