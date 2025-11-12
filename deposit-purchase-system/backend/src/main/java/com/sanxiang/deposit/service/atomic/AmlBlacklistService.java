package com.sanxiang.deposit.service.atomic;

import com.sanxiang.deposit.service.orchestration.AtomicService;
import com.sanxiang.deposit.service.orchestration.ServiceContext;
import com.sanxiang.deposit.service.orchestration.ServiceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("amlBlacklistService")
public class AmlBlacklistService implements AtomicService {
    @Override
    public String getServiceCode() {
        return "S10";
    }

    @Override
    public ServiceResponse execute(ServiceContext context) {
        log.info("S10 AML blacklist verification for user {}", context.getUserId());
        return ServiceResponse.success();
    }
}
