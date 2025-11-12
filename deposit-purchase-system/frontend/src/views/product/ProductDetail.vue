<template>
  <a-card v-if="product" :title="product.productName">
    <a-descriptions :column="1" bordered>
      <a-descriptions-item label="产品编号">{{ product.productCode }}</a-descriptions-item>
      <a-descriptions-item label="利率">{{ product.interestRate }}</a-descriptions-item>
      <a-descriptions-item label="单笔限额">{{ product.singleLimit }}</a-descriptions-item>
      <a-descriptions-item label="日累计限额">{{ product.dailyLimit }}</a-descriptions-item>
      <a-descriptions-item label="风险等级">{{ product.riskLevel }}</a-descriptions-item>
    </a-descriptions>
    <template #extra>
      <a-button type="primary" @click="toPurchase">立即购买</a-button>
    </template>
  </a-card>
</template>

<script setup>
import { onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useProductStore } from '../../store/product';

const store = useProductStore();
const route = useRoute();
const router = useRouter();

const product = computed(() => store.current);

onMounted(async () => {
  await store.fetchProduct(route.params.id);
});

function toPurchase() {
  router.push({ name: 'product-purchase', params: { id: route.params.id } });
}
</script>
