package com.shinchik.cloudkeeper.user.util;

import com.shinchik.cloudkeeper.validation.ValidationProperties;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PasswordValidator implements ConstraintValidator<PasswordConstraint, String> {

    private final ValidationProperties validationProperties;

    @Autowired
    public PasswordValidator(ValidationProperties validationProperties) {
        this.validationProperties = validationProperties;
    }


    @Override
    public void initialize(PasswordConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value.matches(validationProperties.patterns.password);
    }
}
