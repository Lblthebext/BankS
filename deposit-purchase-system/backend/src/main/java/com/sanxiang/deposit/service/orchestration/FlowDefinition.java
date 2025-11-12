package com.sanxiang.deposit.service.orchestration;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class FlowDefinition {
    private List<FlowNode> nodes;

    public Map<String, FlowNode> asMap() {
        Map<String, FlowNode> map = new HashMap<>();
        if (nodes != null) {
            for (FlowNode node : nodes) {
                map.put(node.getId(), node);
            }
        }
        return map;
    }
}
