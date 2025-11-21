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
          <VueFlow v-model:nodes="nodes" v-model:edges="edges" class="flow-canvas" :fit-view="true">
            <Background />
          </VueFlow>
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
import { VueFlow, Background, useVueFlow } from '@vue-flow/core';
import '@vue-flow/core/dist/style.css';
import { Message } from '@arco-design/web-vue';
import { listFlows, saveFlow, publishFlow } from '../../api/orchestration';
import { ensureToken } from '../../utils/token';
import { unwrapResponseData } from '../../utils/response';

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

onMounted(() => {
  bootstrapFlows();
});

async function bootstrapFlows() {
  try {
    await ensureToken();
    const res = await listFlows();
    flowList.value = unwrapResponseData(res, []);
    if (flowList.value.length > 0) {
      selectedFlowCode.value = flowList.value[0].flowCode;
      await loadFlow(selectedFlowCode.value);
    } else {
      resetCanvas();
    }
  } catch (error) {
    console.error('加载流程失败', error);
    Message.error(error?.response?.data?.message || '加载流程失败');
  }
}

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
  if (!flow) {
    selectedFlow.value = null;
    resetCanvas();
    return;
  }
  selectedFlow.value = flow;
  const definition = parseDagDefinition(flow.dagJson);
  const nodeDefs = Array.isArray(definition.nodes) ? definition.nodes : [];
  nodes.value = nodeDefs.map((node, index) => ({
    id: node.id,
    position: { x: 60 + index * 160, y: 100 },
    data: reactive({ async: !!node.async }),
    label: node.id,
    draggable: true
  }));
  rebuildEdges(nodeDefs);
  if (nodes.value.length > 0) {
    selectedNode.value = nodes.value[0];
    hydrateSelection(selectedNode.value.id);
  } else {
    resetSelection();
  }
}

function parseDagDefinition(raw) {
  if (!raw) {
    return { nodes: [] };
  }
  if (typeof raw === 'object') {
    return raw;
  }
  try {
    const trimmed = raw.trim();
    if (!trimmed) {
      return { nodes: [] };
    }
    return JSON.parse(trimmed);
  } catch (error) {
    console.error('解析流程失败', error);
    Message.warning('流程定义数据异常，已展示空画布');
    return { nodes: [] };
  }
}

function rebuildEdges(nodeDefs) {
  edges.value = [];
  nodeDefs.forEach(node => {
    if (node.nextOnSuccess && !node.nextOnSuccess.startsWith('END')) {
      edges.value.push({ id: `${node.id}-success`, source: node.id, target: node.nextOnSuccess, label: '成功' });
    }
    if (node.nextOnFailure && !node.nextOnFailure.startsWith('END')) {
      edges.value.push({ id: `${node.id}-failure`, source: node.id, target: node.nextOnFailure, label: '失败' });
    }
  });
}

function resetCanvas() {
  nodes.value = [];
  edges.value = [];
  resetSelection();
}

function resetSelection() {
  selectedNode.value = null;
  selectedNodeSuccess.value = 'END_SUCCESS';
  selectedNodeFailure.value = 'END_FAIL';
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
  try {
    await ensureToken();
    const res = await saveFlow(payload);
    selectedFlow.value = unwrapResponseData(res, selectedFlow.value);
    await refreshFlows(selectedFlow.value?.flowCode);
    Message.success('流程已保存');
  } catch (error) {
    console.error('保存流程失败', error);
    Message.error(error?.response?.data?.message || '保存流程失败');
  }
}

async function refreshFlows(targetCode = selectedFlowCode.value) {
  await ensureToken();
  const res = await listFlows();
  flowList.value = unwrapResponseData(res, []);
  if (flowList.value.length === 0) {
    selectedFlowCode.value = '';
    selectedFlow.value = null;
    resetCanvas();
    return;
  }
  const nextCode = flowList.value.find(flow => flow.flowCode === targetCode)?.flowCode || flowList.value[0].flowCode;
  selectedFlowCode.value = nextCode;
  await loadFlow(nextCode);
}

async function handlePublish() {
  if (!selectedFlow.value) return;
  try {
    await ensureToken();
    await publishFlow(selectedFlow.value.id);
    Message.success('流程已发布');
  } catch (error) {
    console.error('发布流程失败', error);
    Message.error(error?.response?.data?.message || '发布流程失败');
  }
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
}
.inspector {
  width: 220px;
  border-left: 1px solid #f0f0f0;
  padding-left: 12px;
}
</style>
