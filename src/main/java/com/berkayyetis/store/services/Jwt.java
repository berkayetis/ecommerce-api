package com.berkayyetis.store.services;

import com.berkayyetis.store.entities.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;

import javax.crypto.SecretKey;
import java.util.Date;

@AllArgsConstructor
public class Jwt {
    private final Claims claims;
    private final SecretKey key;

    public boolean isValid(){
        Date expiration = claims.getExpiration();
        return expiration.after(new Date());
    }

    public Long getUserId(){
        return Long.parseLong(claims.getSubject());
    }

    public Role getRole(){
        return Role.valueOf(claims.get("role", String.class));
    }

    public Long getTokenExpiryInSeconds(){
        Date expiration = claims.getExpiration();
        long now = System.currentTimeMillis();
        return (expiration.getTime() - now) / 1000;
    }

    public String toString() {
        return Jwts.builder().claims(claims).signWith(key).compact();
    }
}
