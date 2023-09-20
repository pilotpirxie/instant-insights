package com.instantinsights.api.services;

import com.instantinsights.api.dto.UserDto;
import com.instantinsights.api.entities.User;
import com.instantinsights.api.repositories.PasswordRecoveryRepository;
import com.instantinsights.api.repositories.SessionRepository;
import com.instantinsights.api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    UserRepository userRepository;
    SessionRepository sessionRepository;
    PasswordRecoveryRepository passwordRecoveryRepository;

    @Autowired
    public AccountServiceImpl(
        UserRepository userRepository,
        SessionRepository sessionRepository,
        PasswordRecoveryRepository passwordRecoveryRepository
    ) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.passwordRecoveryRepository = passwordRecoveryRepository;
    }

    @Override
    public List<UserDto> getAllUsersByTeamId(UUID teamId) {
        return userRepository.findAllByTeamId(teamId).stream().map(User::toDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(UUID id) {
        return null;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        return null;
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        return null;
    }

    @Override
    public void deleteUser(UUID id) {

    }

    @Override
    public void resetPassword(UUID userId) {

    }

    @Override
    public void verifyResetPasswordToken(UUID userId, String token) {

    }

    @Override
    public void changePassword(UUID userId, String newPassword) {

    }

    @Override
    public void changeEmail(UUID userId, String newEmail) {

    }

    @Override
    public void verifyEmail(UUID userId, String token) {

    }

    @Override
    public void resendVerificationEmail(UUID userId) {

    }

    @Override
    public void sendResetPasswordEmail(UUID userId) {

    }

    @Override
    public void sendVerificationEmail(UUID userId) {

    }
}
