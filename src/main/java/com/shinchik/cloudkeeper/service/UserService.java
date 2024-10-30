package com.shinchik.cloudkeeper.service;


import com.shinchik.cloudkeeper.model.User;
import com.shinchik.cloudkeeper.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
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
