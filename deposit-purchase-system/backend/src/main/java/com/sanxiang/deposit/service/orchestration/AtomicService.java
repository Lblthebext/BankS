package com.sanxiang.deposit.service.orchestration;

public interface AtomicService {
    String getServiceCode();
    ServiceResponse execute(ServiceContext context) throws Exception;
    default void compensate(ServiceContext context) throws Exception {
    }
}
