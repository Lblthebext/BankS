<template>
  <a-card v-if="product" title="购买产品">
    <a-form :model="form" :rules="rules" layout="vertical" @submit="submit">
      <a-form-item field="amount" label="购买金额">
        <a-input-number v-model="form.amount" :min="1" :step="100" style="width: 100%" />
      </a-form-item>
      <a-form-item field="flowCode" label="购买流程">
        <a-select v-model="form.flowCode" placeholder="请选择流程">
          <a-option v-for="flow in flows" :key="flow.flowCode" :value="flow.flowCode">{{ flow.flowName }}</a-option>
        </a-select>
      </a-form-item>
      <a-form-item field="smsCode" label="短信验证码">
        <a-input v-model="form.smsCode" placeholder="请输入验证码" />
      </a-form-item>
      <a-button type="primary" html-type="submit" :loading="submitting">提交订单</a-button>
    </a-form>
  </a-card>
</template>

<script setup>
import { reactive, ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useProductStore } from '../../store/product';
import { listFlows } from '../../api/orchestration';
import { submitPurchase } from '../../api/purchase';

const route = useRoute();
const router = useRouter();
const store = useProductStore();
const flows = ref([]);
const submitting = ref(false);
const product = computed(() => store.current);

const form = reactive({
  amount: 1000,
  flowCode: '',
  smsCode: '123456'
});

const rules = {
  amount: [{ required: true, message: '请输入金额' }],
  flowCode: [{ required: true, message: '请选择流程' }]
};

onMounted(async () => {
  await store.fetchProduct(route.params.id);
  const res = await listFlows();
  flows.value = res.data;
  if (flows.value.length > 0) {
    form.flowCode = flows.value[0].flowCode;
  }
});

async function submit({ values, errors }) {
  if (errors) return;
  submitting.value = true;
  try {
    const res = await submitPurchase({
      userId: 10001,
      productId: Number(route.params.id),
      amount: values.amount,
      flowCode: values.flowCode,
      smsCode: values.smsCode
    });
    router.push({ name: 'purchase-result', query: { success: res.data.success, orderNo: res.data.orderNo, message: res.data.message } });
  } finally {
    submitting.value = false;
  }
}
</script>
