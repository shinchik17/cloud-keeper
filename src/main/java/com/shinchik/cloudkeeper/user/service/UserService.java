package com.shinchik.cloudkeeper.user.service;


import com.shinchik.cloudkeeper.user.model.User;
import com.shinchik.cloudkeeper.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Profile("auth")
public class UserService {

    private final UserRepository repository;

    @Autowired
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public Optional<User> findByUsername(String username){
        return repository.findByUsername(username);
    }

}
