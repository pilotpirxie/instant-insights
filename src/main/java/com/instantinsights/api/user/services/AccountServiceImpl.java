package com.instantinsights.api.user.services;

import com.instantinsights.api.common.exceptions.NotFoundHttpException;
import com.instantinsights.api.team.entities.Team;
import com.instantinsights.api.team.repositories.TeamRepository;
import com.instantinsights.api.user.dto.SessionDto;
import com.instantinsights.api.user.dto.UserDto;
import com.instantinsights.api.user.entities.PasswordRecovery;
import com.instantinsights.api.user.entities.Session;
import com.instantinsights.api.user.entities.User;
import com.instantinsights.api.user.exceptions.AccountServiceException;
import com.instantinsights.api.user.repositories.PasswordRecoveryRepository;
import com.instantinsights.api.user.repositories.SessionRepository;
import com.instantinsights.api.user.repositories.UserRepository;
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
    public List<UserDto> getAllUsersInTeam(UUID teamId) throws NotFoundHttpException {
        Team team = teamRepository.findById(teamId).orElse(null);
        if (team == null) {
            throw new NotFoundHttpException("Team not found");
        }
        return userRepository.findAllByTeamId(teamId).stream().map(User::toDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getUser(UUID id) throws NotFoundHttpException {
        User user = getUserOrThrow(id);

        return User.toDto(user);
    }

    @Override
    public UserDto getUserByEmail(String email) throws NotFoundHttpException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new NotFoundHttpException("User not found");
        }

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
    public String generatePasswordRecoveryCode(UUID userId) throws NotFoundHttpException {
        getUserOrThrow(userId);

        byte[] code = new byte[16];
        secureRandom.nextBytes(code);

        PasswordRecovery passwordRecovery = new PasswordRecovery();
        passwordRecovery.setCode(encodeBytes(code));
        passwordRecoveryRepository.save(passwordRecovery);

        return encodeBytes(code);
    }

    @Override
    public boolean verifyPasswordRecoveryCode(UUID userId, String code) throws NotFoundHttpException {
        getUserOrThrow(userId);

        PasswordRecovery passwordRecovery = passwordRecoveryRepository.findByCodeAndUserId(code, userId);
        if (passwordRecovery == null) {
            return false;
        }

        passwordRecoveryRepository.delete(passwordRecovery);

        return !passwordRecovery.getCreatedAt().plusMinutes(30).isBefore(LocalDateTime.now());
    }

    @Override
    public void changePassword(UUID userId, String newPassword) throws NotFoundHttpException {
        User user = getUserOrThrow(userId);

        byte[] salt = decodeBytes(user.getSalt());
        String hashedPassword;
        try {
            hashedPassword = getHashedPassword(newPassword, salt);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new NotFoundHttpException("Error hashing password", e);
        }

        user.setPassword(hashedPassword);
        userRepository.save(user);
    }

    @Override
    public void changeEmail(UUID userId, String newEmail) throws NotFoundHttpException, AccountServiceException {
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
    public boolean verifyEmail(UUID userId, String code) throws NotFoundHttpException {
        User user = getUserOrThrow(userId);

        if (!Objects.equals(user.getEmailVerificationCode(), code)) {
            return false;
        }

        user.setEmailVerifiedAt(LocalDateTime.now());
        userRepository.save(user);

        return true;
    }

    @Override
    public void disableTotp(UUID userId) throws NotFoundHttpException {
        User user = getUserOrThrow(userId);

        user.setTotpToken(null);
        userRepository.save(user);
    }

    @Override
    public void enableTotp(UUID userId, String token) throws NotFoundHttpException {
        User user = getUserOrThrow(userId);

        user.setTotpToken(token);
        userRepository.save(user);
    }

    @Override
    public boolean verifyPassword(UUID userId, String password) throws NotFoundHttpException, AccountServiceException {
        User user = getUserOrThrow(userId);

        byte[] salt = decodeBytes(user.getSalt());
        String hashedPassword;
        try {
            hashedPassword = getHashedPassword(password, salt);
            System.out.println(hashedPassword);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new AccountServiceException("Error hashing password", e);
        }

        return Objects.equals(user.getPassword(), hashedPassword);
    }

    @Override
    public boolean checkCredentials(
        String email,
        String password
    ) throws NotFoundHttpException, AccountServiceException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new NotFoundHttpException("User not found");
        }

        return verifyPassword(user.getId(), password);
    }

    @Override
    public SessionDto login(UUID userId) throws NotFoundHttpException {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new NotFoundHttpException("User not found");
        }

        Session session = new Session(
            UUID.randomUUID(),
            getRandomString(32),
            LocalDateTime.now(),
            LocalDateTime.now(),
            userId
        );

        sessionRepository.save(session);

        return Session.toDto(session);
    }

    @Override
    public void logout(UUID userId) {
        sessionRepository.deleteByUserId(userId);
    }

    @Override
    public boolean isEnabled(UUID userId) throws NotFoundHttpException {
        User user = getUserOrThrow(userId);

        return !user.isDisabled();
    }

    @Override
    public void enable(UUID userId) throws NotFoundHttpException {
        User user = getUserOrThrow(userId);

        user.setDisabled(false);
        userRepository.save(user);
    }

    @Override
    public void disable(UUID userId) throws NotFoundHttpException {
        User user = getUserOrThrow(userId);

        user.setDisabled(true);
        userRepository.save(user);
    }

    @Override
    public boolean isVerified(UUID userId) throws NotFoundHttpException {
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

    private User getUserOrThrow(UUID id) throws NotFoundHttpException {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new NotFoundHttpException("User not found");
        }
        return user;
    }

    private String getRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(secureRandom.nextInt(chars.length())));
        }
        return sb.toString();
    }


}
