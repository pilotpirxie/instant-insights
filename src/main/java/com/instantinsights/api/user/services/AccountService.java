package com.instantinsights.api.user.services;

import com.instantinsights.api.common.exceptions.NotFoundException;
import com.instantinsights.api.user.dto.SessionDto;
import com.instantinsights.api.user.dto.UserDto;
import com.instantinsights.api.user.exceptions.AccountServiceException;

import java.util.List;
import java.util.UUID;

public interface AccountService {
    List<UserDto> getAllUsersInTeam(UUID teamId) throws NotFoundException;

    UserDto getUser(UUID id) throws NotFoundException;

    UserDto getUserByEmail(String email) throws NotFoundException;

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

    boolean checkCredentials(String email, String password) throws NotFoundException, AccountServiceException;

    SessionDto login(UUID userId) throws NotFoundException;

    void logout(UUID userId);

    boolean isEnabled(UUID userId) throws NotFoundException;

    void enable(UUID userId) throws NotFoundException;

    void disable(UUID userId) throws NotFoundException;

    boolean isVerified(UUID userId) throws NotFoundException;
}
