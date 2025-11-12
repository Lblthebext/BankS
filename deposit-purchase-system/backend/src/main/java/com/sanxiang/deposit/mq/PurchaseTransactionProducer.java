package com.sanxiang.deposit.mq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PurchaseTransactionProducer {

    private final RocketMQTemplate rocketMQTemplate;

    public void sendHalfMessage(String topic, Map<String, Object> payload) {
        Message<Map<String, Object>> message = MessageBuilder.withPayload(payload).build();
        try {
            rocketMQTemplate.sendMessageInTransaction(topic, message, null);
            log.info("Half message sent to topic {}", topic);
        } catch (Exception ex) {
            log.error("Failed to send transactional message", ex);
        }
    }
}
