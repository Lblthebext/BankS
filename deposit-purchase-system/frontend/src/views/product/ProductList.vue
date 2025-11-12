<template>
  <div>
    <a-card title="存款产品列表" :loading="loading">
      <a-table :data="products" row-key="id">
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
import { onMounted, ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useProductStore } from '../../store/product';
import { fetchToken } from '../../api/auth';

const router = useRouter();
const store = useProductStore();
const loading = ref(false);
const products = computed(() => store.products);

onMounted(async () => {
  loading.value = true;
  await ensureToken();
  await store.fetchProducts();
  loading.value = false;
});

async function ensureToken() {
  if (!localStorage.getItem('deposit-token')) {
    const res = await fetchToken();
    localStorage.setItem('deposit-token', res.data.token);
  }
}

function viewDetail(record) {
  router.push({ name: 'product-detail', params: { id: record.id } });
}
</script>
