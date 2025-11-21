<template>
  <div>
    <a-card title="存款产品列表" :loading="loading">
      <a-table :data="products" row-key="id" :pagination="false">
        <a-table-column title="产品名称" data-index="productName"></a-table-column>
        <a-table-column title="利率" data-index="interestRate"></a-table-column>
        <a-table-column title="风险等级" data-index="riskLevel"></a-table-column>
        <a-table-column title="操作">
          <template #cell="{ record }">
            <a-button type="primary" @click="viewDetail(record)">查看详情</a-button>
          </template>
        </a-table-column>
      </a-table>
    </a-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { Message } from '@arco-design/web-vue';
import { useProductStore } from '../../store/product';
import { ensureToken } from '../../utils/token';
import { storeToRefs } from 'pinia';

const router = useRouter();
const store = useProductStore();
const loading = ref(false);
const { products } = storeToRefs(store);

onMounted(async () => {
  loading.value = true;
  try {
    await ensureToken();
    await store.fetchProducts();
  } catch (error) {
    console.error('加载产品失败', error);
    Message.error(error?.response?.data?.message || '加载产品失败');
  } finally {
    loading.value = false;
  }
});

function viewDetail(record) {
  router.push({ name: 'product-detail', params: { id: record.id } });
}
</script>
