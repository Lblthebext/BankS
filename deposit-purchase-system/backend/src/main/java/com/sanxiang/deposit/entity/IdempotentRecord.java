package com.sanxiang.deposit.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("idempotent_record")
public class IdempotentRecord {
    @TableId
    private Long id;
    private String idempotentId;
    private String requestBody;
    private LocalDateTime createdAt;
}
