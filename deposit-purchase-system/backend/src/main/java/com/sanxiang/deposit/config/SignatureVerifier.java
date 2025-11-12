package com.sanxiang.deposit.config;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class SignatureVerifier {

    private final SignatureProperties properties;

    public SignatureVerifier(SignatureProperties properties) {
        this.properties = properties;
    }

    public boolean verify(String timestamp, String nonce, String body, String signature) {
        if (!properties.isEnabled()) {
            return true;
        }
        try {
            String message = timestamp + "|" + nonce + "|" + body;
            PublicKey publicKey = loadPublicKey(properties.getPublicKey());
            Signature verifier = Signature.getInstance("SHA256withRSA");
            verifier.initVerify(publicKey);
            verifier.update(message.getBytes(StandardCharsets.UTF_8));
            return verifier.verify(Base64.getDecoder().decode(signature));
        } catch (Exception ex) {
            return false;
        }
    }

    private PublicKey loadPublicKey(String key) throws Exception {
        String sanitized = key.replaceAll("-----\\w+ PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(sanitized);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }
}
