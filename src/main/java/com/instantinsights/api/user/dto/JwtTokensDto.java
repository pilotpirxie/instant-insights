package com.instantinsights.api.user.dto;

public record JwtTokensDto(
    String accessToken,
    String refreshToken
) {
}
