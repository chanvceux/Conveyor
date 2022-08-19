package com.neoflex.conveyor.service;

import com.neoflex.conveyor.dto.CreditDTO;
import com.neoflex.conveyor.dto.LoanApplicationRequestDTO;
import com.neoflex.conveyor.dto.LoanOfferDTO;
import com.neoflex.conveyor.dto.ScoringDataDTO;
import com.neoflex.conveyor.exceptionHandler.ScoringException;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

public interface ConveyorService {

    static Integer calculateAge(LocalDate dateOfBirth) {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    static BigDecimal calculateMonthlyPayment(BigDecimal rate, Integer term, BigDecimal amount) {

        BigDecimal monthlyRate = rate.divide(BigDecimal.valueOf(1200), MathContext.DECIMAL128);
        return amount.multiply(monthlyRate.add
                (monthlyRate.divide((BigDecimal.ONE // ? How to use property values in interface
                        .add(monthlyRate))
                        .pow(term)
                        .subtract(BigDecimal.ONE),
                        MathContext.DECIMAL128)));
    }

    List<LoanOfferDTO> offers(LoanApplicationRequestDTO loanApplicationRequestDTO);
    LoanOfferDTO createOffer(LoanApplicationRequestDTO loanApplicationRequestDTO, Boolean isInsuranceEnabled, Boolean isSalaryClient);
    CreditDTO creditCalculation(ScoringDataDTO scoringDataDTO) throws ScoringException;

}
