package com.berkayyetis.store.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class BlacklistService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    public void blacklistToken(String token, long expirySeconds) {
        redisTemplate.opsForValue().set(token, "blacklisted", expirySeconds, TimeUnit.SECONDS);
    }

    public boolean isBlacklisted(String token) {
        System.out.println("isBlacklisted: " + token);
        return redisTemplate.hasKey(token);
    }
}
