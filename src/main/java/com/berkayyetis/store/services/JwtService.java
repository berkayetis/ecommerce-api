package com.berkayyetis.store.services;

import com.berkayyetis.store.configs.JwtConfig;
import com.berkayyetis.store.entities.User;
import io.jsonwebtoken.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@AllArgsConstructor
@Service
public class JwtService {
    private final JwtConfig jwtConfig;
    private final BlacklistService blacklistService;

    public boolean validateToken(String token) {
        try {
            var claims = getClaims(token);
            Jwt jwt = new Jwt(claims, jwtConfig.getSecretKey());
            return jwt.isValid() && !blacklistService.isBlacklisted(token);
        }
        catch (JwtException ex) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        var claims = Jwts.parser()
                .verifyWith(jwtConfig.getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims;
    }

    public Jwt generateAccessToken(User user) {
        var token = generateToken(user, jwtConfig.getAccessTokenExpiration());
        return new Jwt(getClaims(token), jwtConfig.getSecretKey());
    }

    public Jwt generateRefreshToken(User user) {
        var token = generateToken(user, jwtConfig.getRefreshTokenExpiration());
        return new Jwt(getClaims(token), jwtConfig.getSecretKey());
    }

    private String generateToken(User user, long expiration) {
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("username", user.getName())
                .claim("role", user.getRole().name())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * expiration))
                .signWith(jwtConfig.getSecretKey())
                .compact();
    }

    public Jwt parse(String token){
        return new Jwt(getClaims(token), jwtConfig.getSecretKey());
    }
}
