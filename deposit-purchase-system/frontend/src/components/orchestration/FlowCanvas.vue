<template>
  <div>
    <a-card title="服务编排画布">
      <template #extra>
        <a-space>
          <a-select v-model="selectedFlowCode" style="width: 200px" placeholder="选择流程" @change="onFlowChange">
            <a-option v-for="flow in flowList" :key="flow.flowCode" :value="flow.flowCode">{{ flow.flowName }}</a-option>
          </a-select>
          <a-button type="outline" @click="handleSave">保存</a-button>
          <a-button type="primary" @click="handlePublish">发布</a-button>
        </a-space>
      </template>
      <div class="canvas-wrapper">
        <div class="palette">
          <h4>原子服务</h4>
          <a-list :data="atomicServices" :bordered="false">
            <template #item="{ item }">
              <a-tag color="blue" class="palette-item" @click="addServiceNode(item)">{{ item.id }} {{ item.name }}</a-tag>
            </template>
          </a-list>
        </div>
        <div class="canvas">
          <VueFlow v-model:nodes="nodes" v-model:edges="edges" class="flow-canvas" :fit-view="true" />
        </div>
        <div class="inspector" v-if="selectedNode">
          <h4>节点配置：{{ selectedNode.id }}</h4>
          <a-space direction="vertical" fill>
            <div>
              <span>异步节点</span>
              <a-switch v-model="selectedNode.data.async" />
            </div>
            <a-select v-model="selectedNodeSuccess" placeholder="成功路由" allow-clear>
              <a-option value="END_SUCCESS">结束成功</a-option>
              <a-option v-for="node in nodes" :key="node.id" :value="node.id" v-if="node.id !== selectedNode.id">{{ node.id }}</a-option>
            </a-select>
            <a-select v-model="selectedNodeFailure" placeholder="失败路由" allow-clear>
              <a-option value="END_FAIL">结束失败</a-option>
              <a-option v-for="node in nodes" :key="node.id" :value="node.id" v-if="node.id !== selectedNode.id">{{ node.id }}</a-option>
            </a-select>
          </a-space>
        </div>
      </div>
    </a-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue';
import { VueFlow, useVueFlow } from '@vue-flow/core';
import '@vue-flow/core/dist/style.css';
import { listFlows, saveFlow, publishFlow } from '../../api/orchestration';

const atomicServices = [
  { id: 'S1', name: '证件审查接口' },
  { id: 'S2', name: '用户信息校验接口' },
  { id: 'S3', name: '风险等级匹配接口' },
  { id: 'S4', name: '限额校验接口' },
  { id: 'S5', name: '库存锁定接口' },
  { id: 'S6', name: '预扣款接口' },
  { id: 'S7', name: '产品购买接口' },
  { id: 'S8', name: '短信验证码接口' },
  { id: 'S9', name: '活动资格校验接口' },
  { id: 'S10', name: '反洗钱黑名单接口' }
];

const nodes = ref([]);
const edges = ref([]);
const flowList = ref([]);
const selectedFlowCode = ref('');
const selectedFlow = ref(null);
const selectedNode = ref(null);
const selectedNodeSuccess = ref('');
const selectedNodeFailure = ref('');
const hydrating = ref(false);

const { onNodeClick } = useVueFlow({ nodes, edges });

onNodeClick(({ node }) => {
  selectedNode.value = node;
  hydrateSelection(node.id);
});

watch(selectedNodeSuccess, value => {
  if (!selectedNode.value || hydrating.value) return;
  updateEdge(selectedNode.value.id, value, '成功');
});

watch(selectedNodeFailure, value => {
  if (!selectedNode.value || hydrating.value) return;
  updateEdge(selectedNode.value.id, value, '失败');
});

onMounted(async () => {
  const res = await listFlows();
  flowList.value = res.data;
  if (flowList.value.length > 0) {
    selectedFlowCode.value = flowList.value[0].flowCode;
    await loadFlow(selectedFlowCode.value);
  }
});

function addServiceNode(service) {
  if (nodes.value.find(n => n.id === service.id)) {
    return;
  }
  const newNode = {
    id: service.id,
    position: { x: 100 + nodes.value.length * 140, y: 120 },
    data: reactive({ async: false }),
    label: service.id,
    draggable: true
  };
  nodes.value = [...nodes.value, newNode];
  selectedNode.value = newNode;
  hydrateSelection(newNode.id);
}

function hydrateSelection(nodeId) {
  hydrating.value = true;
  const successEdge = edges.value.find(edge => edge.source === nodeId && edge.label === '成功');
  const failureEdge = edges.value.find(edge => edge.source === nodeId && edge.label === '失败');
  selectedNodeSuccess.value = successEdge ? successEdge.target : 'END_SUCCESS';
  selectedNodeFailure.value = failureEdge ? failureEdge.target : 'END_FAIL';
  hydrating.value = false;
}

async function loadFlow(code) {
  const flow = flowList.value.find(f => f.flowCode === code);
  if (!flow) return;
  selectedFlow.value = flow;
  const def = typeof flow.dagJson === 'string' ? JSON.parse(flow.dagJson) : flow.dagJson;
  const nodeDefs = def.nodes || [];
  nodes.value = nodeDefs.map((node, index) => ({
    id: node.id,
    position: { x: 60 + index * 160, y: 100 },
    data: reactive({ async: node.async }),
    label: node.id,
    draggable: true
  }));
  edges.value = [];
  nodeDefs.forEach(node => {
    if (node.nextOnSuccess && !node.nextOnSuccess.startsWith('END')) {
      edges.value.push({ id: `${node.id}-success`, source: node.id, target: node.nextOnSuccess, label: '成功' });
    }
    if (node.nextOnFailure && !node.nextOnFailure.startsWith('END')) {
      edges.value.push({ id: `${node.id}-failure`, source: node.id, target: node.nextOnFailure, label: '失败' });
    }
  });
  if (nodes.value.length > 0) {
    selectedNode.value = nodes.value[0];
    hydrateSelection(selectedNode.value.id);
  } else {
    selectedNode.value = null;
  }
}

function updateEdge(source, target, label) {
  edges.value = edges.value.filter(edge => !(edge.source === source && edge.label === label));
  if (target && !target.startsWith('END')) {
    edges.value = [...edges.value, { id: `${source}-${label}`, source, target, label }];
  }
}

function onFlowChange(value) {
  loadFlow(value);
}

async function handleSave() {
  if (!selectedFlow.value) return;
  const definition = {
    nodes: nodes.value.map(node => {
      const successEdge = edges.value.find(edge => edge.source === node.id && edge.label === '成功');
      const failureEdge = edges.value.find(edge => edge.source === node.id && edge.label === '失败');
      return {
        id: node.id,
        nextOnSuccess: successEdge ? successEdge.target : 'END_SUCCESS',
        nextOnFailure: failureEdge ? failureEdge.target : 'END_FAIL',
        async: !!node.data.async,
        retry: 3,
        timeout: 5000,
        compensation: null
      };
    })
  };
  const payload = { ...selectedFlow.value, dagJson: JSON.stringify(definition) };
  const res = await saveFlow(payload);
  selectedFlow.value = res.data;
  await refreshFlows();
}

async function refreshFlows() {
  const res = await listFlows();
  flowList.value = res.data;
}

async function handlePublish() {
  if (!selectedFlow.value) return;
  await publishFlow(selectedFlow.value.id);
}
</script>

<style scoped>
.canvas-wrapper {
  display: flex;
  gap: 16px;
}
.palette {
  width: 200px;
  border-right: 1px solid #f0f0f0;
  padding-right: 12px;
}
.palette-item {
  display: block;
  margin-bottom: 8px;
  cursor: pointer;
}
.canvas {
  flex: 1;
  height: 480px;
  border: 1px solid #f0f0f0;
}
.flow-canvas {
  width: 100%;
  height: 100%;
  background-image: linear-gradient(90deg, rgba(99, 114, 130, 0.08) 1px, transparent 0),
    linear-gradient(180deg, rgba(99, 114, 130, 0.08) 1px, transparent 0);
  background-size: 20px 20px;
}
.inspector {
  width: 220px;
  border-left: 1px solid #f0f0f0;
  padding-left: 12px;
}
</style>
