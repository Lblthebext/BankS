package com.sanxiang.deposit.service.atomic;

import com.sanxiang.deposit.service.orchestration.AtomicService;
import com.sanxiang.deposit.service.orchestration.ServiceContext;
import com.sanxiang.deposit.service.orchestration.ServiceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserProfileValidateService implements AtomicService {
    @Override
    public String getServiceCode() {
        return "S2";
    }

    @Override
    public ServiceResponse execute(ServiceContext context) {
        log.info("S2 user profile validation for user {}", context.getUserId());
        context.getExt().put("userValidated", true);
        return ServiceResponse.success();
    }
}
