package com.neoflex.conveyor.service;

import com.neoflex.conveyor.dto.CreditDTO;
import com.neoflex.conveyor.dto.LoanApplicationRequestDTO;
import com.neoflex.conveyor.dto.LoanOfferDTO;
import com.neoflex.conveyor.dto.ScoringDataDTO;
import com.neoflex.conveyor.exception_handler.ScoringException;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

public interface ConveyorService {
    BigDecimal one = BigDecimal.ONE;
    BigDecimal zero = BigDecimal.ZERO;
    BigDecimal hundredPercent = BigDecimal.valueOf(100);
    BigDecimal months = BigDecimal.valueOf(12);

    static Integer calculateAge(LocalDate dateOfBirth) {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    static BigDecimal calculateMonthlyPayment(BigDecimal rate, Integer term, BigDecimal amount) {

        BigDecimal monthlyRate = rate.divide(months.multiply(hundredPercent), MathContext.DECIMAL128);

        BigDecimal monthlyPayment = monthlyRate.add(one);
        monthlyPayment = monthlyPayment.pow(term);
        monthlyPayment = monthlyPayment.subtract(one);
        monthlyPayment = monthlyRate.divide(monthlyPayment, MathContext.DECIMAL128);
        monthlyPayment = monthlyRate.add(monthlyPayment);
        monthlyPayment = amount.multiply(monthlyPayment);

        return monthlyPayment.setScale(2, RoundingMode.HALF_UP);
    }

    static BigDecimal calculateMonthlyPercentPayment(BigDecimal remainingDebt, BigDecimal rate) {
        BigDecimal percent = rate.divide(months.multiply(hundredPercent), MathContext.DECIMAL128);
        percent = remainingDebt.multiply(percent);

        return percent.setScale(3, RoundingMode.HALF_UP);
    }

    List<LoanOfferDTO> offers(LoanApplicationRequestDTO loanApplicationRequestDTO);
    LoanOfferDTO createOffer(LoanApplicationRequestDTO loanApplicationRequestDTO, Boolean isInsuranceEnabled, Boolean isSalaryClient);
    CreditDTO creditCalculation(ScoringDataDTO scoringDataDTO) throws ScoringException;

}
