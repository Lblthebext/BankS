package com.sanxiang.deposit.service.orchestration;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class ServiceContext {
    private Long userId;
    private Long productId;
    private BigDecimal amount;
    private String flowCode;
    private String requestId;
    private String smsCode;
    @Builder.Default
    private Map<String, Object> ext = new HashMap<>();

    public String buildIdempotentId() {
        String date = LocalDate.now().toString();
        return productId + ":" + userId + ":" + requestId + ":" + date;
    }

    public static ServiceContext newContext(Long userId, Long productId, BigDecimal amount, String flowCode) {
        return ServiceContext.builder()
                .userId(userId)
                .productId(productId)
                .amount(amount)
                .flowCode(flowCode)
                .requestId(UUID.randomUUID().toString())
                .build();
    }
}
