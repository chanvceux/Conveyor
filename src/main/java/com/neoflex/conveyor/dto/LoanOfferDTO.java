package com.neoflex.conveyor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Builder
@Data
@AllArgsConstructor

public class LoanOfferDTO {

    Long applicationId;
    BigDecimal requestedAmount;
    BigDecimal totalAmount;
    Integer term;
    BigDecimal monthlyPayment;
    BigDecimal rate;
    Boolean isInsuranceEnabled;
    Boolean isSalaryClient;

}
