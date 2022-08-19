package com.neoflex.conveyor.constraint;

import com.neoflex.conveyor.service.ConveyorService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class PersonAgeConstraintValidator implements ConstraintValidator <PersonAgeConstraint, LocalDate> {

    @Override
    public boolean isValid(LocalDate birthDate, ConstraintValidatorContext constraintValidatorContext) {
        return ConveyorService.calculateAge(birthDate) >= 18;
    }
}
