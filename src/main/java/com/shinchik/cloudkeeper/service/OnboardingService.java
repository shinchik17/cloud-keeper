package com.shinchik.cloudkeeper.service;

import com.shinchik.cloudkeeper.model.User;
import com.shinchik.cloudkeeper.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OnboardingService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public OnboardingService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional // TODO: research annotation and its usage
    public void register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        repository.save(user);
    }


}
