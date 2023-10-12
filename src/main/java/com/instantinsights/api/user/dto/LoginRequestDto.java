package com.instantinsights.api.user.dto;

public record LoginRequestDto(String email, String password, String totpCode) {
}
