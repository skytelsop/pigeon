package com.example.pigeon.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator implements ConstraintValidator<ValidEmail, String> {

    private static final String EMAIL_PATTERN = "^[a-z]{1,9}([_a-z0-9-]{1,9})"+
            "@[a-z]{1,9}(.[a-z]{1,3})(.[a-z]{1,2})$";

    private static final Pattern PATTERN = Pattern.compile(EMAIL_PATTERN);

    @Override
    public void initialize(ValidEmail constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(final String email, final ConstraintValidatorContext context) {
        return (validateEmail(email));
    }

    private boolean validateEmail(final String email) {

        Matcher matcher = PATTERN.matcher(email);
        return matcher.matches();
    }
}
