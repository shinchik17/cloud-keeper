package com.shinchik.cloudkeeper.util;

import com.shinchik.cloudkeeper.model.User;
import com.shinchik.cloudkeeper.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

@Component
public class UserValidator implements Validator {

    private final UserService service;

    @Autowired
    public UserValidator(UserService service) {
        this.service = service;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User)target;

        Optional<User> optUser = service.findByUsername(user.getUsername());

        if (optUser.isPresent()) {
            errors.rejectValue("username", "", "User with such username already exists.");
        }

    }
}
