package com.instantinsights.api.user.dto;

public record JwtTokensResponseDto(
    String accessToken,
    String refreshToken
) {
}
