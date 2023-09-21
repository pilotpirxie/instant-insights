package com.instantinsights.api.services;

import com.instantinsights.api.dto.UserDto;
import com.instantinsights.api.exceptions.AccountServiceException;

import java.util.List;
import java.util.UUID;

public interface AccountService {
    List<UserDto> getAllUsersByTeamId(UUID teamId);

    UserDto getUserById(UUID id);

    void createUser(UserDto userDto, String password) throws AccountServiceException;

    void updateUser(UserDto userDto);

    void deleteUser(UUID id);

    void resetPassword(UUID userId);

    boolean verifyResetPasswordToken(UUID userId, String token);

    void changePassword(UUID userId, String newPassword);

    void changeEmail(UUID userId, String newEmail);

    boolean verifyEmail(UUID userId, String token);

    void sendResetPasswordEmail(UUID userId);

    void sendVerificationEmail(UUID userId);

    void initiateTotp(UUID userId);

    void disableTotp(UUID userId);

    boolean verifyTotp(UUID userId, String token);

    boolean verifyPassword(UUID userId, String password);

    void logout(UUID userId);

    boolean isEnabled(UUID userId);

    void enable(UUID userId);

    void disable(UUID userId);

    boolean isVerified(UUID userId);
}
