package com.sanxiang.deposit.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchaseRequest {
    @NotNull
    private Long productId;
    @NotNull
    private Long userId;
    @NotNull
    @Min(1)
    private BigDecimal amount;
    @NotBlank
    private String flowCode;
    private String smsCode;
}
