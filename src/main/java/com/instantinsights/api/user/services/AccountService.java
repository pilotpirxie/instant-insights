package com.instantinsights.api.user.services;

import com.instantinsights.api.common.exceptions.NotFoundHttpException;
import com.instantinsights.api.user.dto.SessionDto;
import com.instantinsights.api.user.dto.UserDto;
import com.instantinsights.api.user.exceptions.AccountServiceException;

import java.util.List;
import java.util.UUID;

public interface AccountService {
    List<UserDto> getAllUsersInTeam(UUID teamId) throws NotFoundHttpException;

    UserDto getUser(UUID id) throws NotFoundHttpException;

    UserDto getUserByEmail(String email) throws NotFoundHttpException;

    void createUser(UserDto userDto, String password) throws AccountServiceException;

    void updateUser(UserDto userDto);

    void deleteUser(UUID id);

    String generatePasswordRecoveryCode(UUID userId) throws NotFoundHttpException;

    boolean verifyPasswordRecoveryCode(UUID userId, String code) throws NotFoundHttpException;

    void changePassword(UUID userId, String newPassword) throws NotFoundHttpException;

    void changeEmail(UUID userId, String newEmail) throws NotFoundHttpException, AccountServiceException;

    boolean verifyEmail(UUID userId, String code) throws NotFoundHttpException;

    void enableTotp(UUID userId, String token) throws NotFoundHttpException;

    void disableTotp(UUID userId) throws NotFoundHttpException;

    boolean verifyPassword(UUID userId, String password) throws NotFoundHttpException, AccountServiceException;

    boolean checkCredentials(String email, String password) throws NotFoundHttpException, AccountServiceException;

    SessionDto login(UUID userId) throws NotFoundHttpException;

    void logout(UUID userId);

    boolean isEnabled(UUID userId) throws NotFoundHttpException;

    void enable(UUID userId) throws NotFoundHttpException;

    void disable(UUID userId) throws NotFoundHttpException;

    boolean isVerified(UUID userId) throws NotFoundHttpException;
}
