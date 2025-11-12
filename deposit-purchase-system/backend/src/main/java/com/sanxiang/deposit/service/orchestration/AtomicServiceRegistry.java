package com.sanxiang.deposit.service.orchestration;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AtomicServiceRegistry {

    private final Map<String, AtomicService> serviceMap = new HashMap<>();

    public AtomicServiceRegistry(List<AtomicService> services) {
        for (AtomicService service : services) {
            serviceMap.put(service.getServiceCode(), service);
        }
    }

    public AtomicService getService(String code) {
        return serviceMap.get(code);
    }
}
