package com.sanxiang.deposit.controller;

import com.sanxiang.deposit.config.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/token")
    public ResponseEntity<Map<String, String>> token(@RequestBody Map<String, Object> payload) {
        String userId = String.valueOf(payload.getOrDefault("userId", "10001"));
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", payload.getOrDefault("role", "USER"));
        String token = jwtService.createToken(userId, claims);
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok(response);
    }
}
