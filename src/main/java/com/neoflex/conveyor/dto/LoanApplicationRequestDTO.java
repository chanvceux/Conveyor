package com.neoflex.conveyor.dto;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class LoanApplicationRequestDTO {

    Long application_id;
    BigDecimal amount;
    Integer term;
    String firstName;
    String lastName;
    String middleName;
    String email;
    LocalDate birthdate;
    String passportSeries;
    String passportNumber;

}
