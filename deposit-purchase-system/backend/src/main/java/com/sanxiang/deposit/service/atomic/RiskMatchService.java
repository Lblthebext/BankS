package com.sanxiang.deposit.service.atomic;

import com.sanxiang.deposit.service.orchestration.AtomicService;
import com.sanxiang.deposit.service.orchestration.ServiceContext;
import com.sanxiang.deposit.service.orchestration.ServiceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RiskMatchService implements AtomicService {
    @Override
    public String getServiceCode() {
        return "S3";
    }

    @Override
    public ServiceResponse execute(ServiceContext context) {
        log.info("S3 risk level matching for user {}", context.getUserId());
        context.getExt().put("riskMatch", true);
        return ServiceResponse.success();
    }
}
