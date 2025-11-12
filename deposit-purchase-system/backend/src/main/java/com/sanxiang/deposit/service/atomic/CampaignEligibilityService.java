package com.sanxiang.deposit.service.atomic;

import com.sanxiang.deposit.service.orchestration.AtomicService;
import com.sanxiang.deposit.service.orchestration.ServiceContext;
import com.sanxiang.deposit.service.orchestration.ServiceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("campaignEligibilityService")
public class CampaignEligibilityService implements AtomicService {
    @Override
    public String getServiceCode() {
        return "S9";
    }

    @Override
    public ServiceResponse execute(ServiceContext context) {
        log.info("S9 campaign eligibility checked for user {}", context.getUserId());
        return ServiceResponse.success();
    }
}
