package com.sanxiang.deposit.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("orchestration_flow")
public class OrchestrationFlow {
    @TableId
    private Long id;
    private String flowCode;
    private String flowName;
    private String description;
    private String dagJson;
    private Integer status;
    private Integer version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
