package com.sanxiang.deposit.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("async_task_record")
public class AsyncTaskRecord {
    @TableId
    private Long id;
    private String taskId;
    private String flowCode;
    private String nodeId;
    private String status;
    private String payload;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
