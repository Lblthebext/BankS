import http from './http';

export function listFlows() {
  return http.get('/orchestration/flows');
}

export function saveFlow(flow) {
  return http.post('/orchestration/saveFlow', flow);
}

export function publishFlow(id) {
  return http.post(`/orchestration/publish/${id}`);
}
