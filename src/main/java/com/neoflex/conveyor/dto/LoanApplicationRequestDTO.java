package com.neoflex.conveyor.dto;
import com.neoflex.conveyor.constraint.PersonAgeConstraint;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class LoanApplicationRequestDTO {

    @NotNull
    @DecimalMin("10000")
    BigDecimal amount;

    @NotNull
    @Min(6)
    Integer term;

    @NotBlank
    @Length(min = 2, max = 30)
    @Pattern(regexp = "[a-zA-Z]+$")
    String firstName;

    @NotBlank
    @Length(min = 2, max = 30)
    @Pattern(regexp = "[a-zA-Z]+$")
    String lastName;

    @Length(min = 2, max = 30)
    @Pattern(regexp = "[a-zA-Z]+$")
    String middleName;

    @NotBlank
    @Pattern(regexp = "^[a-z0-9._-]+@[a-z0-9.-]+\\.[a-z]{2,20}$")
    String email;

    @NotNull
    @PersonAgeConstraint
    @DateTimeFormat(pattern = "yyy-MM-dd")
    LocalDate birthdate;

    @NotBlank
    @Length(min = 4, max = 4)
    @Pattern(regexp = "^[0-9]+$")
    String passportSeries;

    @NotBlank
    @Length(min = 6, max = 6)
    @Pattern(regexp = "^[0-9]+$")
    String passportNumber;

}
