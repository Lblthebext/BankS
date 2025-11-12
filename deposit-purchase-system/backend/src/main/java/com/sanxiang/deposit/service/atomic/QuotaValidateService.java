package com.sanxiang.deposit.service.atomic;

import com.sanxiang.deposit.entity.DepositProduct;
import com.sanxiang.deposit.mapper.DepositProductMapper;
import com.sanxiang.deposit.service.orchestration.AtomicService;
import com.sanxiang.deposit.service.orchestration.ServiceContext;
import com.sanxiang.deposit.service.orchestration.ServiceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class QuotaValidateService implements AtomicService {

    private final DepositProductMapper productMapper;

    public QuotaValidateService(DepositProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    @Override
    public String getServiceCode() {
        return "S4";
    }

    @Override
    public ServiceResponse execute(ServiceContext context) {
        DepositProduct product = productMapper.selectById(context.getProductId());
        if (product == null) {
            return ServiceResponse.failure("Product not found");
        }
        BigDecimal amount = context.getAmount();
        if (amount.compareTo(product.getSingleLimit()) > 0 || amount.compareTo(product.getMinAmount()) < 0) {
            return ServiceResponse.failure("Amount out of range");
        }
        log.info("S4 quota validation passed for user {}", context.getUserId());
        return ServiceResponse.success();
    }
}
