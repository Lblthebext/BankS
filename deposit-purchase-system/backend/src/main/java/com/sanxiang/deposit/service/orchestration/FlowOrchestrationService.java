package com.sanxiang.deposit.service.orchestration;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanxiang.deposit.entity.OrchestrationFlow;
import com.sanxiang.deposit.mapper.OrchestrationFlowMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
public class FlowOrchestrationService {

    private static final String FLOW_CACHE_KEY = "deposit:flow:";

    private final OrchestrationFlowMapper flowMapper;
    private final FlowExecutor flowExecutor;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public FlowOrchestrationService(OrchestrationFlowMapper flowMapper,
                                    FlowExecutor flowExecutor,
                                    StringRedisTemplate redisTemplate) {
        this.flowMapper = flowMapper;
        this.flowExecutor = flowExecutor;
        this.redisTemplate = redisTemplate;
    }

    public List<OrchestrationFlow> listActiveFlows() {
        return flowMapper.selectList(new LambdaQueryWrapper<OrchestrationFlow>().eq(OrchestrationFlow::getStatus, 1));
    }

    @Cacheable(value = "flow", key = "#flowCode")
    public FlowDefinition loadFlow(String flowCode) {
        String cacheValue = redisTemplate.opsForValue().get(FLOW_CACHE_KEY + flowCode);
        if (cacheValue != null) {
            try {
                return objectMapper.readValue(cacheValue, FlowDefinition.class);
            } catch (JsonProcessingException ignored) {
            }
        }
        OrchestrationFlow flow = flowMapper.selectOne(new LambdaQueryWrapper<OrchestrationFlow>().eq(OrchestrationFlow::getFlowCode, flowCode));
        if (flow == null) {
            throw new IllegalArgumentException("Flow not found: " + flowCode);
        }
        try {
            FlowDefinition definition = objectMapper.readValue(flow.getDagJson(), FlowDefinition.class);
            redisTemplate.opsForValue().set(FLOW_CACHE_KEY + flowCode, objectMapper.writeValueAsString(definition), Duration.ofMinutes(10));
            return definition;
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Invalid DAG definition", e);
        }
    }

    public FlowExecutionResult executeFlow(String flowCode, ServiceContext context) {
        FlowDefinition definition = loadFlow(flowCode);
        context.setFlowCode(flowCode);
        return flowExecutor.execute(definition, context);
    }

    @Transactional
    public OrchestrationFlow saveFlow(OrchestrationFlow flow) {
        if (flow.getId() == null) {
            flowMapper.insert(flow);
        } else {
            flowMapper.updateById(flow);
        }
        return flow;
    }

    @Transactional
    public void publishFlow(Long flowId) {
        OrchestrationFlow flow = Optional.ofNullable(flowMapper.selectById(flowId))
                .orElseThrow(() -> new IllegalArgumentException("Flow not found"));
        try {
            FlowDefinition definition = objectMapper.readValue(flow.getDagJson(), FlowDefinition.class);
            redisTemplate.opsForValue().set(FLOW_CACHE_KEY + flow.getFlowCode(), objectMapper.writeValueAsString(definition), Duration.ofMinutes(30));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Invalid DAG definition", e);
        }
    }
}
