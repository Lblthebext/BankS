package com.sanxiang.deposit.controller;

import com.sanxiang.deposit.entity.OrchestrationFlow;
import com.sanxiang.deposit.service.orchestration.FlowOrchestrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orchestration")
public class FlowController {

    private final FlowOrchestrationService orchestrationService;

    public FlowController(FlowOrchestrationService orchestrationService) {
        this.orchestrationService = orchestrationService;
    }

    @GetMapping("/flows")
    public ResponseEntity<List<OrchestrationFlow>> listFlows() {
        return ResponseEntity.ok(orchestrationService.listActiveFlows());
    }

    @PostMapping("/saveFlow")
    public ResponseEntity<OrchestrationFlow> saveFlow(@RequestBody OrchestrationFlow flow) {
        return ResponseEntity.ok(orchestrationService.saveFlow(flow));
    }

    @PostMapping("/publish/{flowId}")
    public ResponseEntity<Void> publish(@PathVariable Long flowId) {
        orchestrationService.publishFlow(flowId);
        return ResponseEntity.ok().build();
    }
}
