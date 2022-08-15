package com.neoflex.conveyor.constraints;

import com.neoflex.conveyor.services.ConveyorServices;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class PersonAgeConstraintValidator implements ConstraintValidator <PersonAgeConstraint, LocalDate> {

    @Override
    public boolean isValid(LocalDate birthDate, ConstraintValidatorContext constraintValidatorContext) {
        return ConveyorServices.calculateAge(birthDate) >= 18;
    }
}
