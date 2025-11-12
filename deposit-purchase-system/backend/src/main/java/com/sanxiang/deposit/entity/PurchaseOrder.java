package com.sanxiang.deposit.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("purchase_order")
public class PurchaseOrder {
    @TableId
    private Long id;
    private String orderNo;
    private Long productId;
    private Long userId;
    private BigDecimal amount;
    private String flowCode;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
