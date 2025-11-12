<template>
  <a-result :status="status" :title="title" :sub-title="subTitle">
    <template #extra>
      <a-space>
        <a-button type="primary" @click="goList">返回列表</a-button>
      </a-space>
    </template>
  </a-result>
</template>

<script setup>
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const route = useRoute();
const router = useRouter();

const success = computed(() => route.query.success === 'true' || route.query.success === true);
const orderNo = computed(() => route.query.orderNo);

const status = computed(() => (success.value ? 'success' : 'error'));
const title = computed(() => (success.value ? '购买成功' : '购买失败'));
const subTitle = computed(() => (success.value ? `订单号：${orderNo.value}` : route.query.message || '请稍后重试'));

function goList() {
  router.push({ name: 'product-list' });
}
</script>
