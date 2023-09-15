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
}
