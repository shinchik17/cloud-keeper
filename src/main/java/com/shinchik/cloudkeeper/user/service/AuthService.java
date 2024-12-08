package com.shinchik.cloudkeeper.user.service;

import com.shinchik.cloudkeeper.user.model.Role;
import com.shinchik.cloudkeeper.user.model.User;
import com.shinchik.cloudkeeper.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Profile("auth")
public class AuthService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional // TODO: research annotation and its usage
    public void register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        repository.save(user);
    }


}
