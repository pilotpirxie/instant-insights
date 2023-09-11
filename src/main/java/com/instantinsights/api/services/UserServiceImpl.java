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
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(UUID id) {
        User user = userRepository.findById(id).orElse(null);
        assert user != null;
        return convertToDto(user);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = convertToEntity(userDto);
        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        UserDto existingUserDto = getUserById(userDto.id());
        if (existingUserDto == null) {
            return null;
        }

        User user = convertToEntity(userDto);
        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    @Override
    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

    private UserDto convertToDto(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getSalt(),
                user.getEmailVerificationCode(),
                user.getEmailVerifiedAt(),
                user.getRegisterIp(),
                user.isDisabled(),
                user.getTotpToken(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    private User convertToEntity(UserDto userDto) {
        return new User(
                userDto.id(),
                userDto.email(),
                userDto.password(),
                userDto.salt(),
                userDto.emailVerificationCode(),
                userDto.emailVerifiedAt(),
                userDto.registerIp(),
                userDto.isDisabled(),
                userDto.totpToken(),
                userDto.createdAt(),
                userDto.updatedAt()
        );
    }
}
