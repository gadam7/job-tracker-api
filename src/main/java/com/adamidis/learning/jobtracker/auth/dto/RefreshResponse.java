package com.adamidis.learning.jobtracker.auth.dto;

public record RefreshResponse(String accessToken, String refreshToken, String tokenType) {
}
