package com.instantinsights.api.services;

import com.instantinsights.api.dto.UserDto;
import com.instantinsights.api.entities.PasswordRecovery;
import com.instantinsights.api.entities.Team;
import com.instantinsights.api.entities.User;
import com.instantinsights.api.exceptions.AccountServiceException;
import com.instantinsights.api.exceptions.NotFoundException;
import com.instantinsights.api.repositories.PasswordRecoveryRepository;
import com.instantinsights.api.repositories.SessionRepository;
import com.instantinsights.api.repositories.TeamRepository;
import com.instantinsights.api.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {
    SecureRandom secureRandom = new SecureRandom();
    TeamRepository teamRepository;
    UserRepository userRepository;
    SessionRepository sessionRepository;
    PasswordRecoveryRepository passwordRecoveryRepository;

    public AccountServiceImpl(
        TeamRepository teamRepository,
        UserRepository userRepository,
        SessionRepository sessionRepository,
        PasswordRecoveryRepository passwordRecoveryRepository
    ) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.passwordRecoveryRepository = passwordRecoveryRepository;
    }

    @Override
    public List<UserDto> getAllUsersInTeam(UUID teamId) throws NotFoundException {
        Team team = teamRepository.findById(teamId).orElse(null);
        if (team == null) {
            throw new NotFoundException("Team not found");
        }
        return userRepository.findAllByTeamId(teamId).stream().map(User::toDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getUser(UUID id) throws NotFoundException {
        User user = getUserOrThrow(id);

        return User.toDto(user);
    }

    @Transactional
    @Override
    public void createUser(UserDto userDto, String password) throws AccountServiceException {
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);

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

    @Transactional
    @Override
    public void updateUser(UserDto userDto) {
        userRepository.save(User.fromDto(userDto));
    }

    @Override
    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

    @Override
    public String generatePasswordRecoveryCode(UUID userId) throws NotFoundException {
        getUserOrThrow(userId);

        byte[] code = new byte[16];
        secureRandom.nextBytes(code);

        PasswordRecovery passwordRecovery = new PasswordRecovery();
        passwordRecovery.setCode(encodeBytes(code));
        passwordRecoveryRepository.save(passwordRecovery);

        return encodeBytes(code);
    }

    @Override
    public boolean verifyPasswordRecoveryCode(UUID userId, String code) throws NotFoundException {
        getUserOrThrow(userId);

        PasswordRecovery passwordRecovery = passwordRecoveryRepository.findByCodeAndUserId(code, userId);
        if (passwordRecovery == null) {
            return false;
        }

        passwordRecoveryRepository.delete(passwordRecovery);

        return !passwordRecovery.getCreatedAt().plusMinutes(30).isBefore(LocalDateTime.now());
    }

    @Override
    public void changePassword(UUID userId, String newPassword) throws NotFoundException {
        User user = getUserOrThrow(userId);

        byte[] salt = decodeBytes(user.getSalt());
        String hashedPassword;
        try {
            hashedPassword = getHashedPassword(newPassword, salt);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new NotFoundException("Error hashing password", e);
        }

        user.setPassword(hashedPassword);
        userRepository.save(user);
    }

    @Override
    public void changeEmail(UUID userId, String newEmail) throws NotFoundException, AccountServiceException {
        User user = getUserOrThrow(userId);

        User existingUser = userRepository.findByEmail(newEmail);
        if (existingUser != null) {
            throw new AccountServiceException("Email already in use");
        }

        user.setEmail(newEmail);
        user.setEmailVerifiedAt(null);
        userRepository.save(user);
    }

    @Override
    public boolean verifyEmail(UUID userId, String code) throws NotFoundException {
        User user = getUserOrThrow(userId);

        if (!Objects.equals(user.getEmailVerificationCode(), code)) {
            return false;
        }

        user.setEmailVerifiedAt(LocalDateTime.now());
        userRepository.save(user);

        return true;
    }

    @Override
    public void disableTotp(UUID userId) throws NotFoundException {
        User user = getUserOrThrow(userId);

        user.setTotpToken(null);
        userRepository.save(user);
    }

    @Override
    public void enableTotp(UUID userId, String token) throws NotFoundException {
        User user = getUserOrThrow(userId);

        user.setTotpToken(token);
        userRepository.save(user);
    }

    @Override
    public boolean verifyPassword(UUID userId, String password) throws NotFoundException, AccountServiceException {
        User user = getUserOrThrow(userId);

        byte[] salt = decodeBytes(user.getSalt());
        String hashedPassword;
        try {
            hashedPassword = getHashedPassword(password, salt);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new AccountServiceException("Error hashing password", e);
        }

        return Objects.equals(user.getPassword(), hashedPassword);
    }

    @Override
    public void logout(UUID userId) {
        sessionRepository.deleteByUserId(userId);
    }

    @Override
    public boolean isEnabled(UUID userId) throws NotFoundException {
        User user = getUserOrThrow(userId);

        return !user.isDisabled();
    }

    @Override
    public void enable(UUID userId) throws NotFoundException {
        User user = getUserOrThrow(userId);

        user.setDisabled(false);
        userRepository.save(user);
    }

    @Override
    public void disable(UUID userId) throws NotFoundException {
        User user = getUserOrThrow(userId);

        user.setDisabled(true);
        userRepository.save(user);
    }

    @Override
    public boolean isVerified(UUID userId) throws NotFoundException {
        User user = getUserOrThrow(userId);

        return user.getEmailVerifiedAt() != null;
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
        return encodeBytes(hash);
    }

    private User getUserOrThrow(UUID id) throws NotFoundException {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        return user;
    }
}
