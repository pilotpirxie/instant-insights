package com.instantinsights.api.services;

import com.instantinsights.api.dto.UserDto;
import com.instantinsights.api.exceptions.AccountServiceException;
import com.instantinsights.api.exceptions.NotFoundException;

import java.util.List;
import java.util.UUID;

public interface AccountService {
    List<UserDto> getAllUsersInTeam(UUID teamId) throws NotFoundException;

    UserDto getUser(UUID id) throws NotFoundException;

    void createUser(UserDto userDto, String password) throws AccountServiceException;

    void updateUser(UserDto userDto);

    void deleteUser(UUID id);

    String generatePasswordRecoveryCode(UUID userId) throws NotFoundException;

    boolean verifyPasswordRecoveryCode(UUID userId, String code) throws NotFoundException;

    void changePassword(UUID userId, String newPassword) throws NotFoundException;

    void changeEmail(UUID userId, String newEmail) throws NotFoundException, AccountServiceException;

    boolean verifyEmail(UUID userId, String code) throws NotFoundException;

    void enableTotp(UUID userId, String token) throws NotFoundException;

    void disableTotp(UUID userId) throws NotFoundException;

    boolean verifyPassword(UUID userId, String password) throws NotFoundException, AccountServiceException;

    void logout(UUID userId);

    boolean isEnabled(UUID userId) throws NotFoundException;

    void enable(UUID userId) throws NotFoundException;

    void disable(UUID userId) throws NotFoundException;

    boolean isVerified(UUID userId) throws NotFoundException;
}
