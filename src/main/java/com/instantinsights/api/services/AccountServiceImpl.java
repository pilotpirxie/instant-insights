package com.instantinsights.api.services;

import com.instantinsights.api.dto.UserDto;
import com.instantinsights.api.entities.User;
import com.instantinsights.api.exceptions.AccountServiceException;
import com.instantinsights.api.repositories.PasswordRecoveryRepository;
import com.instantinsights.api.repositories.SessionRepository;
import com.instantinsights.api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
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
        return userRepository.findById(id).map(User::toDto).orElse(null);
    }

    @Override
    public void createUser(UserDto userDto, String password) throws AccountServiceException {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);

        String hashedPassword;
        try {
            hashedPassword = getHashedPassword(password, salt);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new AccountServiceException("Error hashing password", e);
        }

        User user = User.fromDto(userDto);
        user.setSalt(encodeBytes(salt));
        user.setPassword(hashedPassword);
        userRepository.save(User.fromDto(userDto));
    }

    @Override
    public void updateUser(UserDto userDto) {

    }

    @Override
    public void deleteUser(UUID id) {

    }

    @Override
    public void resetPassword(UUID userId) {

    }

    @Override
    public boolean verifyResetPasswordToken(UUID userId, String token) {
        return false;
    }

    @Override
    public void changePassword(UUID userId, String newPassword) {

    }

    @Override
    public void changeEmail(UUID userId, String newEmail) {

    }

    @Override
    public boolean verifyEmail(UUID userId, String token) {
        return false;
    }

    @Override
    public void sendResetPasswordEmail(UUID userId) {

    }

    @Override
    public void sendVerificationEmail(UUID userId) {

    }

    @Override
    public void initiateTotp(UUID userId) {
    }

    @Override
    public void disableTotp(UUID userId) {
    }

    @Override
    public boolean verifyTotp(UUID userId, String token) {
        return false;
    }

    @Override
    public boolean verifyPassword(UUID userId, String password) {
        return false;
    }

    @Override
    public void logout(UUID userId) {

    }

    @Override
    public boolean isEnabled(UUID userId) {
        return false;
    }

    @Override
    public void enable(UUID userId) {

    }

    @Override
    public void disable(UUID userId) {

    }

    @Override
    public boolean isVerified(UUID userId) {
        return false;
    }
 
    static String encodeBytes(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    static byte[] decodeBytes(String str) {
        return Base64.getDecoder().decode(str);
    }

    static String getHashedPassword(
        String password,
        byte[] salt
    ) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = factory.generateSecret(spec).getEncoded();
        return new String(hash);
    }
}
