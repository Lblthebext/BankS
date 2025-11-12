package com.sanxiang.deposit.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "deposit.signature")
public class SignatureProperties {
    private String publicKey;
    private boolean enabled = true;
}
