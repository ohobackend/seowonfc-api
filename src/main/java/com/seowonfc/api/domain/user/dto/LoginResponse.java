package com.seowonfc.api.domain.user.dto;

public record LoginResponse(String accessToken, String refreshToken, UserResponse user) {}