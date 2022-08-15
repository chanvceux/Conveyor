package com.neoflex.conveyor.services;

import com.neoflex.conveyor.dtos.CreditDTO;
import com.neoflex.conveyor.dtos.LoanApplicationRequestDTO;
import com.neoflex.conveyor.dtos.LoanOfferDTO;
import com.neoflex.conveyor.dtos.ScoringDataDTO;
import com.neoflex.conveyor.exceptionHandlers.ScoringException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

public interface ConveyorServices {

    static Integer calculateAge(LocalDate dateOfBirth) {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    };

    static BigDecimal calculateMonthlyPayment(BigDecimal rate, Integer term, BigDecimal amount) {

        BigDecimal monthlyRate = rate.divide(BigDecimal.valueOf(1200), 5, RoundingMode.HALF_UP);
        return amount.multiply(monthlyRate.add(monthlyRate.divide((BigDecimal.valueOf(1).add(monthlyRate)).
                pow(term).subtract(BigDecimal.valueOf(1)), 5, RoundingMode.HALF_UP)));
    }

    List<LoanOfferDTO> offers(LoanApplicationRequestDTO loanApplicationRequestDTO);
    LoanOfferDTO createOffer(LoanApplicationRequestDTO loanApplicationRequestDTO, Boolean isInsuranceEnabled, Boolean isSalaryClient);
    CreditDTO creditCalculation(ScoringDataDTO scoringDataDTO) throws ScoringException;

}
