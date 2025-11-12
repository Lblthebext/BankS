package com.sanxiang.deposit.service.atomic;

import com.sanxiang.deposit.mq.PurchaseTransactionProducer;
import com.sanxiang.deposit.service.orchestration.AtomicService;
import com.sanxiang.deposit.service.orchestration.ServiceContext;
import com.sanxiang.deposit.service.orchestration.ServiceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service("preDebitService")
public class PreDebitService implements AtomicService {

    private final PurchaseTransactionProducer producer;

    public PreDebitService(PurchaseTransactionProducer producer) {
        this.producer = producer;
    }

    @Override
    public String getServiceCode() {
        return "S6";
    }

    @Override
    public ServiceResponse execute(ServiceContext context) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", context.getUserId());
        payload.put("productId", context.getProductId());
        payload.put("amount", context.getAmount());
        producer.sendHalfMessage("deposit-purchase", payload);
        log.info("S6 pre debit success for user {}", context.getUserId());
        return ServiceResponse.success();
    }

    @Override
    public void compensate(ServiceContext context) {
        log.info("Compensate pre debit for user {}", context.getUserId());
    }
}
