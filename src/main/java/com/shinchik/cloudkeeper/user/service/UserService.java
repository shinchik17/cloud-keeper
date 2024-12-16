package com.shinchik.cloudkeeper.user.service;


import com.shinchik.cloudkeeper.user.mapper.UserMapper;
import com.shinchik.cloudkeeper.user.model.User;
import com.shinchik.cloudkeeper.user.model.UserDto;
import com.shinchik.cloudkeeper.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Profile("auth")
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findByUsername(String username){
        return repository.findByUsername(username);
    }

    @Transactional
    public void register(UserDto user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        repository.save(UserMapper.INSTANCE.mapToEntity(user));
    }

}
