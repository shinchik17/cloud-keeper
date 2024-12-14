package com.shinchik.cloudkeeper.user.validation;

import com.shinchik.cloudkeeper.user.model.User;
import com.shinchik.cloudkeeper.user.model.UserDto;
import com.shinchik.cloudkeeper.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

@Component
@Profile("auth")
public class UserDtoValidator implements Validator {

    private final UserService service;

    @Autowired
    public UserDtoValidator(UserService service) {
        this.service = service;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return UserDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserDto userDto = (UserDto) target;

        Optional<User> optUser = service.findByUsername(userDto.getUsername());

        if (optUser.isPresent()) {
            errors.rejectValue("username",
                    "NORMAL", "User with username '%s' already exists".formatted(userDto.getUsername()));
        }

        if (!passwordsMatch(userDto)) {
            errors.rejectValue("password", "NORMAL", "Passwords do not match");
        }

    }

    private static boolean passwordsMatch(UserDto userDto) {
        return userDto.getPassword().matches(userDto.getPasswordConfirmation());
    }
}
