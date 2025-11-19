package com.sanxiang.deposit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class SecurityInterceptor implements HandlerInterceptor {

    private static final String ATTRIBUTE_CLAIMS = "JWT_CLAIMS";

    private final JwtService jwtService;
    private final SignatureVerifier signatureVerifier;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SecurityInterceptor(JwtService jwtService, SignatureVerifier signatureVerifier) {
        this.jwtService = jwtService;
        this.signatureVerifier = signatureVerifier;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }
        String path = request.getRequestURI();
        if (isPublicPath(path)) {
            return true;
        }
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            unauthorized(response, "Missing token");
            return false;
        }
        String token = authorization.substring(7);
        Claims claims;
        try {
            claims = jwtService.parseToken(token);
        } catch (Exception ex) {
            unauthorized(response, "Invalid token");
            return false;
        }
        String timestamp = request.getHeader("X-Sign-Timestamp");
        String nonce = request.getHeader("X-Sign-Nonce");
        String signature = request.getHeader("X-Signature");
        String bodyDigest = request.getRequestURI();
        if (!signatureVerifier.verify(timestamp == null ? "" : timestamp,
                nonce == null ? "" : nonce,
                bodyDigest,
                signature == null ? "" : signature)) {
            unauthorized(response, "Signature verification failed");
            return false;
        }
        request.setAttribute(ATTRIBUTE_CLAIMS, claims);
        return true;
    }

    private boolean isPublicPath(String path) {
        if (path == null) {
            return false;
        }
        return "/products".equals(path)
                || path.startsWith("/products/")
                || "/api/products".equals(path)
                || path.startsWith("/api/products/");
    }

    private void unauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        Map<String, Object> payload = new HashMap<>();
        payload.put("code", 401);
        payload.put("message", message);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(payload));
    }
}
