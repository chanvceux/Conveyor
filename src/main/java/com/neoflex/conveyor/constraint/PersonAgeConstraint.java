package com.neoflex.conveyor.constraint;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PersonAgeConstraintValidator.class)

public @interface PersonAgeConstraint {

    String message() default "Age < 18 years";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
