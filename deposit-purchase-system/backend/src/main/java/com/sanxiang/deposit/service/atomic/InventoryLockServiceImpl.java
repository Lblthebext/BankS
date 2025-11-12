package com.sanxiang.deposit.service.atomic;

import com.sanxiang.deposit.lock.InventoryLockService;
import com.sanxiang.deposit.service.orchestration.AtomicService;
import com.sanxiang.deposit.service.orchestration.ServiceContext;
import com.sanxiang.deposit.service.orchestration.ServiceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("inventoryLockService")
public class InventoryLockServiceImpl implements AtomicService {

    private final InventoryLockService inventoryLockService;

    public InventoryLockServiceImpl(InventoryLockService inventoryLockService) {
        this.inventoryLockService = inventoryLockService;
    }

    @Override
    public String getServiceCode() {
        return "S5";
    }

    @Override
    public ServiceResponse execute(ServiceContext context) {
        String bucket = String.valueOf(context.getUserId() % 4);
        boolean locked = inventoryLockService.lockInventory(String.valueOf(context.getProductId()), bucket, 1);
        if (!locked) {
            return ServiceResponse.failure("Inventory insufficient");
        }
        log.info("S5 inventory locked for product {} bucket {}", context.getProductId(), bucket);
        context.getExt().put("inventoryBucket", bucket);
        return ServiceResponse.success();
    }

    @Override
    public void compensate(ServiceContext context) {
        log.info("Compensate inventory for product {}", context.getProductId());
    }
}
