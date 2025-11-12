import { createRouter, createWebHistory } from 'vue-router';

const routes = [
  {
    path: '/',
    name: 'product-list',
    component: () => import('../views/product/ProductList.vue'),
    meta: { menu: 'products' }
  },
  {
    path: '/product/:id',
    name: 'product-detail',
    component: () => import('../views/product/ProductDetail.vue'),
    meta: { menu: 'products' }
  },
  {
    path: '/purchase/:id',
    name: 'product-purchase',
    component: () => import('../views/product/ProductPurchase.vue'),
    meta: { menu: 'products' }
  },
  {
    path: '/result',
    name: 'purchase-result',
    component: () => import('../views/product/PurchaseResult.vue'),
    meta: { menu: 'products' }
  },
  {
    path: '/orchestration',
    name: 'orchestration',
    component: () => import('../components/orchestration/FlowCanvas.vue'),
    meta: { menu: 'orchestration' }
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;
