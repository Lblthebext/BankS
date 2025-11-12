package com.sanxiang.deposit.service.orchestration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlowExecutionResult {
    private boolean success;
    private String message;
    private Map<String, Object> context;

    public static FlowExecutionResult success(Map<String, Object> context) {
        return new FlowExecutionResult(true, "SUCCESS", context);
    }

    public static FlowExecutionResult failure(String message, Map<String, Object> context) {
        return new FlowExecutionResult(false, message, context);
    }

    public static FlowExecutionResult failure(String message) {
        return new FlowExecutionResult(false, message, new HashMap<>());
    }
}
