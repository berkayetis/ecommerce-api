package com.berkayyetis.store.controllers;

import com.berkayyetis.store.configs.JwtConfig;
import com.berkayyetis.store.services.BlacklistService;
import com.berkayyetis.store.services.Jwt;
import com.berkayyetis.store.dtos.JwtResponse;
import com.berkayyetis.store.dtos.UserDto;
import com.berkayyetis.store.dtos.UserLoginRequest;
import com.berkayyetis.store.entities.User;
import com.berkayyetis.store.mapper.UserMapper;
import com.berkayyetis.store.repositories.UserRepository;
import com.berkayyetis.store.services.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final BlacklistService blacklistService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtConfig jwtConfig;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> loginUser(
            @Valid @RequestBody UserLoginRequest userLoginRequest,
            HttpServletResponse response) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                userLoginRequest.getEmail(),
                userLoginRequest.getPassword()
        ));

        var user = userRepository.findByEmail(userLoginRequest.getEmail()).orElseThrow();

        Jwt jwtAccessToken = jwtService.generateAccessToken(user);
        Jwt jwtRefreshToken = jwtService.generateRefreshToken(user);

        Cookie cookie = new Cookie("refreshToken", jwtRefreshToken.toString());
        cookie.setHttpOnly(true);
        cookie.setPath("/auth");
        cookie.setMaxAge(jwtConfig.getRefreshTokenExpiration()); // 7d
        cookie.setSecure(true);
        response.addCookie(cookie);

        return ResponseEntity.ok(new JwtResponse(jwtAccessToken.toString()));
    }
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(name = "refreshToken") String refreshToken,
                                       HttpServletResponse response) {

        System.out.println(refreshToken);
        var jwt = jwtService.parse(refreshToken);
        if (refreshToken != null) {
            // 2. add to redis
            blacklistService.blacklistToken(refreshToken, jwt.getTokenExpiryInSeconds());
        }

        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .path("/auth")
                .maxAge(0)
                .httpOnly(true)
                .build();

        System.out.println("deleteCookie: " + deleteCookie);
        response.addHeader("Set-Cookie", deleteCookie.toString());

        blacklistService.blacklistToken(refreshToken, jwt.getTokenExpiryInSeconds());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> me() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var id = (String) authentication.getPrincipal();

        var user = userRepository.findById(Long.parseLong(id)).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        var userDto = userMapper.toDto(user);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(@CookieValue(name = "refreshToken") String refreshToken) {
        var jwt = jwtService.parse(refreshToken);
        if(jwt == null || !jwt.isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var userId = jwt.getUserId();
        User user = userRepository.findById(userId).orElseThrow();

        var jwtAccessToken = jwtService.generateAccessToken(user);
        return ResponseEntity.ok(new JwtResponse(jwtAccessToken.toString()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Void> handleBadCredentialsException(BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
