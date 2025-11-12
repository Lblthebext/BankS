package com.sanxiang.deposit.service.orchestration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanxiang.deposit.entity.AsyncTaskRecord;
import com.sanxiang.deposit.mapper.AsyncTaskRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class FlowExecutor {

    private final AtomicServiceRegistry serviceRegistry;
    private final AsyncTaskRecordMapper asyncTaskRecordMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public FlowExecutor(AtomicServiceRegistry serviceRegistry, AsyncTaskRecordMapper asyncTaskRecordMapper) {
        this.serviceRegistry = serviceRegistry;
        this.asyncTaskRecordMapper = asyncTaskRecordMapper;
    }

    @Transactional
    public FlowExecutionResult execute(FlowDefinition definition, ServiceContext context) {
        Map<String, FlowNode> nodeMap = definition.asMap();
        if (definition.getNodes() == null || definition.getNodes().isEmpty()) {
            return FlowExecutionResult.failure("Flow definition is empty");
        }
        String currentNodeId = definition.getNodes().get(0).getId();
        Deque<AtomicService> executedStack = new ArrayDeque<>();
        Map<String, Object> shared = new HashMap<>();
        while (!"END_SUCCESS".equals(currentNodeId) && !"END_FAIL".equals(currentNodeId)) {
            FlowNode node = nodeMap.get(currentNodeId);
            if (node == null) {
                return FlowExecutionResult.failure("Unknown node: " + currentNodeId, shared);
            }
            AtomicService service = serviceRegistry.getService(node.getId());
            if (service == null) {
                return FlowExecutionResult.failure("Service not registered: " + node.getId(), shared);
            }
            boolean success = false;
            ServiceResponse response = null;
            for (int attempt = 1; attempt <= Math.max(1, node.getRetry()); attempt++) {
                try {
                    CompletableFuture<ServiceResponse> future = CompletableFuture.supplyAsync(() -> {
                        try {
                            return service.execute(context);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
                    response = future.get(node.getTimeout(), TimeUnit.MILLISECONDS);
                    if (response != null && response.isSuccess()) {
                        success = true;
                        break;
                    }
                } catch (Exception ex) {
                    log.warn("Service {} attempt {} failed: {}", node.getId(), attempt, ex.getMessage());
                    try {
                        TimeUnit.MILLISECONDS.sleep((long) Math.pow(2, attempt - 1) * 200L);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
            if (!success) {
                compensate(executedStack, context);
                return FlowExecutionResult.failure("Node execution failed: " + currentNodeId, shared);
            }
            executedStack.push(service);
            if (response != null && response.getPayload() != null) {
                shared.putAll(response.getPayload());
            }
            if (node.isAsync()) {
                recordAsyncTask(context, node, shared);
            }
            currentNodeId = success ? node.getNextOnSuccess() : node.getNextOnFailure();
        }
        if ("END_SUCCESS".equals(currentNodeId)) {
            return FlowExecutionResult.success(shared);
        }
        compensate(executedStack, context);
        return FlowExecutionResult.failure("Flow ended with failure", shared);
    }

    private void recordAsyncTask(ServiceContext context, FlowNode node, Map<String, Object> shared) {
        try {
            AsyncTaskRecord record = new AsyncTaskRecord();
            record.setTaskId(UUID.randomUUID().toString());
            record.setFlowCode(context.getFlowCode());
            record.setNodeId(node.getId());
            record.setStatus("PENDING");
            record.setPayload(objectMapper.writeValueAsString(shared));
            record.setCreatedAt(LocalDateTime.now());
            record.setUpdatedAt(LocalDateTime.now());
            asyncTaskRecordMapper.insert(record);
        } catch (Exception ex) {
            log.error("Failed to persist async task", ex);
        }
    }

    private void compensate(Deque<AtomicService> executedStack, ServiceContext context) {
        while (!executedStack.isEmpty()) {
            AtomicService service = executedStack.pop();
            try {
                service.compensate(context);
            } catch (Exception ex) {
                log.error("Compensation failed for service {}", service.getServiceCode(), ex);
            }
        }
    }
}
