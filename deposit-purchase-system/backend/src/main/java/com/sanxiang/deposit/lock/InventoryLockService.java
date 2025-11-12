package com.sanxiang.deposit.lock;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class InventoryLockService {

    private static final String LUA_SCRIPT = "local stock = redis.call('HGET', KEYS[1], ARGV[1])\n" +
            "if not stock then return -1 end\n" +
            "stock = tonumber(stock)\n" +
            "if stock < tonumber(ARGV[2]) then return 0 end\n" +
            "redis.call('HINCRBY', KEYS[1], ARGV[1], 0 - tonumber(ARGV[2]))\n" +
            "return 1";

    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<Long> script;

    public InventoryLockService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.script = new DefaultRedisScript<>(LUA_SCRIPT, Long.class);
    }

    public boolean lockInventory(String productCode, String bucket, int quantity) {
        Long result = redisTemplate.execute(script, Collections.singletonList("deposit:inventory:" + productCode), bucket, String.valueOf(quantity));
        return result != null && result == 1L;
    }
}
