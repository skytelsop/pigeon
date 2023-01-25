package com.example.pigeon.validation;

import com.example.pigeon.web.dto.UserDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(final PasswordMatches constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {

        final UserDto user = (UserDto) value;
        return user.getPassword().equals(user.getMatchingPassword());
    }
}
