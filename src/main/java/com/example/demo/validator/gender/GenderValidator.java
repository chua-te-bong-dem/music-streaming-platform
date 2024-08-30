package com.example.demo.validator.gender;

import com.example.demo.constant.GenderEnum;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class GenderValidator implements ConstraintValidator<GenderValid, GenderEnum> {
    @Override
    public void initialize(GenderValid constraintAnnotation) {}

    @Override
    public boolean isValid(GenderEnum genderEnum, ConstraintValidatorContext constraintValidatorContext) {
        return genderEnum.name().matches("^(?)(MALE|FEMALE|OTHER)$");
    }
}

