package com.sanxiang.deposit.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("atomic_service")
public class AtomicServiceDefinition {
    @TableId
    private Long id;
    private String serviceCode;
    private String serviceName;
    private String beanName;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
