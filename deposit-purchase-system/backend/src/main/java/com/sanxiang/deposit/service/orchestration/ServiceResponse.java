package com.sanxiang.deposit.service.orchestration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceResponse {
    private boolean success;
    private String message;
    private Map<String, Object> payload;

    public static ServiceResponse success() {
        return new ServiceResponse(true, "OK", new HashMap<>());
    }

    public static ServiceResponse failure(String message) {
        return new ServiceResponse(false, message, new HashMap<>());
    }

    public ServiceResponse with(String key, Object value) {
        if (payload == null) {
            payload = new HashMap<>();
        }
        payload.put(key, value);
        return this;
    }
}
