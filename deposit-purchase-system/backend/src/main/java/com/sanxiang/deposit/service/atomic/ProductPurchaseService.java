package com.sanxiang.deposit.service.atomic;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.sanxiang.deposit.entity.PurchaseOrder;
import com.sanxiang.deposit.mapper.PurchaseOrderMapper;
import com.sanxiang.deposit.service.orchestration.AtomicService;
import com.sanxiang.deposit.service.orchestration.ServiceContext;
import com.sanxiang.deposit.service.orchestration.ServiceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service("productPurchaseService")
public class ProductPurchaseService implements AtomicService {

    private final PurchaseOrderMapper orderMapper;

    public ProductPurchaseService(PurchaseOrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    @Override
    public String getServiceCode() {
        return "S7";
    }

    @Override
    public ServiceResponse execute(ServiceContext context) {
        PurchaseOrder order = new PurchaseOrder();
        order.setId(IdWorker.getId());
        order.setOrderNo("DP" + order.getId());
        order.setUserId(context.getUserId());
        order.setProductId(context.getProductId());
        order.setAmount(context.getAmount());
        order.setFlowCode(context.getFlowCode());
        order.setStatus("SUCCESS");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        orderMapper.insert(order);
        log.info("S7 order created {}", order.getOrderNo());
        return ServiceResponse.success().with("orderNo", order.getOrderNo());
    }
}
