package com.adamidis.learning.jobtracker.auth;

import com.adamidis.learning.jobtracker.auth.dto.AuthResponse;
import com.adamidis.learning.jobtracker.auth.dto.LoginRequest;
import com.adamidis.learning.jobtracker.auth.dto.RefreshResponse;
import com.adamidis.learning.jobtracker.auth.dto.RegisterRequest;
import com.adamidis.learning.jobtracker.repository.RoleRepository;
import com.adamidis.learning.jobtracker.repository.UserRepository;
import com.adamidis.learning.jobtracker.security.CustomUserDetailsService;
import com.adamidis.learning.jobtracker.security.JwtService;
import com.adamidis.learning.jobtracker.user.Role;
import com.adamidis.learning.jobtracker.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already exists");
        }

        Role defaultRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalArgumentException("Default role USER not found"));

        User user = User.builder()
                .name(request.name())
                .email(request.email().toLowerCase())
                .passwordHash(passwordEncoder.encode(request.password()))
                .enabled(true)
                .roles(Set.of(defaultRole))
                .build();

        User savedUser = userRepository.save(user);
        User loadedUser = userRepository.findByEmail(savedUser.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found after registration"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(loadedUser.getEmail());

        var roles = loadedUser.getRoles().stream().map(Role::getName).toList();
        var privileges = loadedUser.getRoles().stream()
                .flatMap(role -> role.getPrivileges().stream())
                .map(privilege -> privilege.getName())
                .distinct()
                .toList();

        String accessToken = jwtService.generateAccessToken(userDetails, loadedUser.getId(), roles, privileges);
        String refreshToken = jwtService.generateRefreshToken(userDetails, loadedUser.getId(), privileges);

        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                loadedUser.getId(),
                loadedUser.getName(),
                loadedUser.getEmail()
        );
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        var roles = user.getRoles().stream().map(Role::getName).toList();
        var privileges = user.getRoles().stream()
                .flatMap(role -> role.getPrivileges().stream())
                .map(privilege -> privilege.getName())
                .distinct()
                .toList();

        String accessToken = jwtService.generateAccessToken(userDetails, user.getId(), roles, privileges);
        String refreshToken = jwtService.generateRefreshToken(userDetails, user.getId(), privileges);

        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public AuthResponse refreshToken(String refreshToken) {
        String email = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        if (!jwtService.isRefreshTokenValid(refreshToken) || !jwtService.isTokenValid(refreshToken, userDetails)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        var roles = user.getRoles().stream().map(Role::getName).toList();
        var privileges = user.getRoles().stream()
                .flatMap(role -> role.getPrivileges().stream())
                .map(privilege -> privilege.getName())
                .distinct()
                .toList();

        String newAccess = jwtService.generateAccessToken(userDetails, user.getId(), roles, privileges);
        String newRefresh = jwtService.generateRefreshToken(userDetails, user.getId(), privileges);

        return new AuthResponse(newAccess, newRefresh, "Bearer", user.getId(), user.getName(), user.getEmail());
    }
}