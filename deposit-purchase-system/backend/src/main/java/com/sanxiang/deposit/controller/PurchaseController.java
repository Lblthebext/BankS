package com.sanxiang.deposit.controller;

import com.sanxiang.deposit.aop.Idempotent;
import com.sanxiang.deposit.controller.dto.PurchaseRequest;
import com.sanxiang.deposit.controller.dto.PurchaseResponse;
import com.sanxiang.deposit.service.orchestration.FlowExecutionResult;
import com.sanxiang.deposit.service.orchestration.FlowOrchestrationService;
import com.sanxiang.deposit.service.orchestration.ServiceContext;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/purchase")
public class PurchaseController {

    private final FlowOrchestrationService orchestrationService;

    public PurchaseController(FlowOrchestrationService orchestrationService) {
        this.orchestrationService = orchestrationService;
    }

    @PostMapping
    @Idempotent(key = "#request.userId + ':' + #request.productId + ':' + #request.flowCode + ':' + #request.amount")
    public ResponseEntity<PurchaseResponse> purchase(@Valid @RequestBody PurchaseRequest request) {
        ServiceContext context = ServiceContext.builder()
                .userId(request.getUserId())
                .productId(request.getProductId())
                .amount(request.getAmount())
                .flowCode(request.getFlowCode())
                .requestId(UUID.randomUUID().toString())
                .build();
        context.getExt().put("smsCode", request.getSmsCode());
        FlowExecutionResult result = orchestrationService.executeFlow(request.getFlowCode(), context);
        if (result.isSuccess()) {
            String orderNo = result.getContext() == null ? null : (String) result.getContext().get("orderNo");
            return ResponseEntity.ok(new PurchaseResponse(true, "SUCCESS", orderNo));
        }
        return ResponseEntity.ok(new PurchaseResponse(false, result.getMessage(), null));
    }
}
