package com.instantinsights.api.user.dto;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserDto(
    UUID id,
    String email,
    LocalDateTime emailVerifiedAt,
    InetAddress registerIp,
    boolean isDisabled,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
