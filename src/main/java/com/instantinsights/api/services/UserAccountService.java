package com.instantinsights.api.services;

import com.instantinsights.api.dto.UserDto;
import com.instantinsights.api.entities.User;
import com.instantinsights.api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserAccountService implements AccountService {

    private final UserRepository userRepository;

    @Autowired
    public UserAccountService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDto> getAllUsersByAppId(UUID appId) {
        return null;
    }

    @Override
    public List<UserDto> getAllUsersByTeamId(UUID teamId) {
        return null;
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
    public void deleteUserFromTeam(UUID userId, UUID teamId) {

    }

    @Override
    public void addUserToTeam(UUID userId, UUID teamId) {

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
