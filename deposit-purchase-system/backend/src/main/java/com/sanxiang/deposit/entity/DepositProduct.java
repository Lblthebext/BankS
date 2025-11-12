package com.sanxiang.deposit.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("deposit_product")
public class DepositProduct {
    @TableId
    private Long id;
    private String productCode;
    private String productName;
    private BigDecimal interestRate;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private BigDecimal dailyLimit;
    private BigDecimal singleLimit;
    private String riskLevel;
    private Integer status;
    private String attributes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
