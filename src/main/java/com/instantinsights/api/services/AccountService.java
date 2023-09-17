package com.instantinsights.api.services;

import com.instantinsights.api.dto.UserDto;

import java.util.List;
import java.util.UUID;

public interface AccountService {
    List<UserDto> getAllUsersByAppId(UUID appId);

    List<UserDto> getAllUsersByTeamId(UUID teamId);

    UserDto getUserById(UUID id);

    UserDto createUser(UserDto userDto);

    UserDto updateUser(UserDto userDto);

    void deleteUser(UUID id);

    void deleteUserFromTeam(UUID userId, UUID teamId);

    void addUserToTeam(UUID userId, UUID teamId);

    void resetPassword(UUID userId);

    void verifyResetPasswordToken(UUID userId, String token);

    void changePassword(UUID userId, String newPassword);

    void changeEmail(UUID userId, String newEmail);

    void verifyEmail(UUID userId, String token);

    void resendVerificationEmail(UUID userId);

    void sendResetPasswordEmail(UUID userId);

    void sendVerificationEmail(UUID userId);
}
