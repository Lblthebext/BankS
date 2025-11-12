package com.sanxiang.deposit.service.atomic;

import com.sanxiang.deposit.service.orchestration.AtomicService;
import com.sanxiang.deposit.service.orchestration.ServiceContext;
import com.sanxiang.deposit.service.orchestration.ServiceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("smsOtpService")
public class SmsOtpService implements AtomicService {
    @Override
    public String getServiceCode() {
        return "S8";
    }

    @Override
    public ServiceResponse execute(ServiceContext context) {
        String smsCode = (String) context.getExt().getOrDefault("smsCode", "123456");
        if (!"123456".equals(smsCode)) {
            return ServiceResponse.failure("SMS verification failed");
        }
        log.info("S8 sms otp verified for user {}", context.getUserId());
        return ServiceResponse.success();
    }
}
