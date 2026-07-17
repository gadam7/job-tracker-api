package com.adamidis.learning.jobtracker.auth;

import com.adamidis.learning.jobtracker.auth.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public AuthResponse refreshToken(@RequestBody RefreshTokenRequest request) {
        System.out.println("REFRESH HIT");
        System.out.println("TOKEN NULL? " + (request == null || request.refreshToken() == null));

        if (request == null || request.refreshToken() == null) {
            throw new IllegalArgumentException("Refresh token is required");
        }

        return authService.refreshToken(request.refreshToken());
    }
}
