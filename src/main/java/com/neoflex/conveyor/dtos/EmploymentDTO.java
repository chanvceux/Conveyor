package com.neoflex.conveyor.dtos;

import com.neoflex.conveyor.enums.EmploymentStatus;
import com.neoflex.conveyor.enums.Position;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
@AllArgsConstructor

public class EmploymentDTO {

    EmploymentStatus employmentStatus;
    String employerINN;
    BigDecimal salary;
    Position position;
    Integer workExperienceTotal;
    Integer workExperienceCurrent;

}
