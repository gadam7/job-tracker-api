package com.adamidis.learning.jobtracker.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtService {

    public static final String JAVA_LEARNING_SA = "java-learning-sa";
    public static final String JOB_TRACKER = "job-tracker";

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration-ms}") // 1 hour expiration
    private long accessExpirationMs;

    @Value("${jwt.refresh-expiration-ms}") // 24 hours expiration
    private long refreshExpirationMs;

    public String generateAccessToken(UserDetails userDetails, Long userId, Collection<String> roles, Collection<String> privileges) {
        return JWT.create().withIssuer(JAVA_LEARNING_SA)
                .withAudience(JOB_TRACKER)
                .withClaim("uid", userId)
                .withSubject(String.valueOf(userDetails.getUsername()))
                .withClaim("roles", new ArrayList<>(roles))
                .withClaim("privileges", new ArrayList<>(privileges))
                .withClaim("type", "access")
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + accessExpirationMs))
                .sign(Algorithm.HMAC256(jwtSecret.getBytes()));
    }

    public String generateRefreshToken(UserDetails userDetails, Long userId, Collection<String> privileges) {
        return JWT.create().withIssuer(JAVA_LEARNING_SA)
                .withAudience(JOB_TRACKER)
                .withClaim("uid", userId)
                .withSubject(String.valueOf(userDetails.getUsername()))
                .withClaim("privileges", new ArrayList<>(privileges))
                .withClaim("type", "refresh")
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshExpirationMs))
                .sign(Algorithm.HMAC256(jwtSecret.getBytes()));
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isRefreshTokenValid(String token) {
        String type = extractClaim(token, claims -> claims.get("type", String.class));
        return type.equals("refresh");
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return resolver.apply(claims);
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
}
