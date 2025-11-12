package com.sanxiang.deposit.service.orchestration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlowNode {
    private String id;
    private String nextOnSuccess;
    private String nextOnFailure;
    private boolean async;
    private int retry = 3;
    private int timeout = 5000;
    private String compensation;
}
