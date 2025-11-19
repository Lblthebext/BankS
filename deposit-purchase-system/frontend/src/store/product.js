import { defineStore } from 'pinia';
import { getProducts, getProductDetail } from '../api/product';
import { unwrapResponseData } from '../utils/response';

export const useProductStore = defineStore('product', {
  state: () => ({
    products: [],
    current: null
  }),
  actions: {
    async fetchProducts() {
      const res = await getProducts();
      this.products = unwrapResponseData(res, []);
    },
    async fetchProduct(id) {
      const res = await getProductDetail(id);
      this.current = unwrapResponseData(res, null);
    }
  }
});
