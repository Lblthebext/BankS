import { defineStore } from 'pinia';
import { getProducts, getProductDetail } from '../api/product';

export const useProductStore = defineStore('product', {
  state: () => ({
    products: [],
    current: null
  }),
  actions: {
    async fetchProducts() {
      const res = await getProducts();
      this.products = res.data;
    },
    async fetchProduct(id) {
      const res = await getProductDetail(id);
      this.current = res.data;
    }
  }
});
