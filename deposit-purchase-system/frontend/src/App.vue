<template>
  <a-layout style="min-height: 100vh">
    <a-layout-header>
      <div class="logo">三湘银行存款产品购买平台</div>
    </a-layout-header>
    <a-layout>
      <a-layout-sider :width="240">
        <a-menu mode="vertical" :selected-keys="[selectedKey]" @menu-item-click="onMenuClick">
          <a-menu-item key="products">产品列表</a-menu-item>
          <a-menu-item key="orchestration">服务编排</a-menu-item>
        </a-menu>
      </a-layout-sider>
      <a-layout-content style="padding: 24px">
        <router-view />
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router';
import { computed } from 'vue';

const route = useRoute();
const router = useRouter();

const selectedKey = computed(() => route.meta.menu || 'products');

function onMenuClick(key) {
  if (key === 'orchestration') {
    router.push({ name: 'orchestration' });
  } else {
    router.push({ name: 'product-list' });
  }
}
</script>

<style scoped>
.logo {
  color: #fff;
  font-size: 18px;
}
</style>
