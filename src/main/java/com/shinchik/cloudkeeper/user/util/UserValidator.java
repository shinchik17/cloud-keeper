package com.shinchik.cloudkeeper.user.util;

import com.shinchik.cloudkeeper.user.model.User;
import com.shinchik.cloudkeeper.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

@Component
@Profile("auth")
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
            errors.rejectValue("username",
                    "NORMAL", "User with username '%s' already exists".formatted(user.getUsername()));
        }

    }

    public void validatePasswordsMatch(Object target, Errors errors, String passConfirmation){
        User user = (User)target;

        if (!user.passwordsMatch(passConfirmation)){
            errors.rejectValue("password", "NORMAL", "Passwords do not match");
        }

    }
}
