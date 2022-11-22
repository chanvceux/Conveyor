package com.neoflex.conveyor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Builder 
@Data
@AllArgsConstructor

public class CreditDTO {

    BigDecimal amount;
    Integer term;
    BigDecimal monthlyPayment;
    BigDecimal rate;
    BigDecimal psk;
    Boolean isInsuranceEnabled;
    Boolean isSalaryClient;
    List<PaymentScheduleElementDTO> paymentSchedule;

}
