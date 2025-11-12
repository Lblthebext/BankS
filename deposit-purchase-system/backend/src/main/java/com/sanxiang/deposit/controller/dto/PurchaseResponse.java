package com.sanxiang.deposit.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PurchaseResponse {
    private boolean success;
    private String message;
    private String orderNo;
}
