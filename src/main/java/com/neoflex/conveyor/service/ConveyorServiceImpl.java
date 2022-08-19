package com.neoflex.conveyor.service;

import com.neoflex.conveyor.dto.*;
import com.neoflex.conveyor.exceptionHandler.ScoringException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
public class ConveyorServiceImpl implements ConveyorService {

    @Value("${service.rate}")
    BigDecimal rate;

    @Value("${service.insurancePercent}")
    BigDecimal insurancePercent;

    @Value("${service.changeRateByOne}")
    BigDecimal changeRateByOne;

    @Value("${service.changeRateByTwo}")
    BigDecimal changeRateByTwo;

    @Value("${service.changeRateByThree}")
    BigDecimal changeRateByThree;

    @Value("${service.changeRateByFour}")
    BigDecimal changeRateByFour;

    @Override
    public List<LoanOfferDTO> offers(LoanApplicationRequestDTO loanApplicationRequestDTO) {

        List<LoanOfferDTO> loanOfferDTOS = new ArrayList<>();

        log.trace("ADDING offer to List<loanOfferDTOS> by executing createOffer(): isInsuranceEnabled TRUE, isSalaryClient TRUE");
        loanOfferDTOS.add(createOffer(loanApplicationRequestDTO, true, true));

        log.trace("ADDING offer to loanOfferDTOS List by executing createOffer(): isInsuranceEnabled FALSE, isSalaryClient FALSE");
        loanOfferDTOS.add(createOffer(loanApplicationRequestDTO, false, false));

        log.trace("ADDING offer to loanOfferDTOS List by executing createOffer(): isInsuranceEnabled TRUE, isSalaryClient FALSE");
        loanOfferDTOS.add(createOffer(loanApplicationRequestDTO, true, false));

        log.trace("ADDING offer to loanOfferDTOS List by executing createOffer(): isInsuranceEnabled FALSE, isSalaryClient TRUE");
        loanOfferDTOS.add(createOffer(loanApplicationRequestDTO, false, true));
        loanOfferDTOS.sort(Comparator.comparing(LoanOfferDTO::getRate).reversed());

        log.trace("RETURNING List<loanOfferDTOs>, size: {}", loanOfferDTOS.size());
        log.debug("OUTPUT VALUES: {}", loanOfferDTOS);
        return loanOfferDTOS;
    }

    @Override
    public LoanOfferDTO createOffer(LoanApplicationRequestDTO loanApplicationRequestDTO,
                                    Boolean isInsuranceEnabled, Boolean isSalaryClient) {

        BigDecimal requestedAmount = loanApplicationRequestDTO.getAmount();
        BigDecimal totalRate = rate;

        if (isSalaryClient.equals(true)) {
            totalRate = totalRate.subtract(changeRateByOne);
            log.trace("CHANGING RATE. Subtracting by 1, caused by: isSalaryClient TRUE, current value: {}", totalRate);
        }

        if (isInsuranceEnabled.equals(true)) {
            totalRate = totalRate.subtract(changeRateByThree);
            log.trace("CHANGING RATE. Subtracting by 3, caused by: isInsuranceEnabled TRUE, current value: {}", totalRate);
            requestedAmount = requestedAmount.add(requestedAmount.multiply(insurancePercent));
            log.trace("CHANGING AMOUNT. Increasing by 5 percent, caused by: isInsuranceEnabled TRUE, current value: {}", requestedAmount);
        }

        BigDecimal monthlyPayment = ConveyorService.calculateMonthlyPayment(totalRate, loanApplicationRequestDTO.getTerm(), requestedAmount);
        log.trace("EXECUTION calculateMonthlyPayment(). Returned monthlyPayment value: {}", monthlyPayment);


        LoanOfferDTO loanOfferDTO = LoanOfferDTO.builder()
                //.applicationId()
                .requestedAmount(requestedAmount)
                .totalAmount(monthlyPayment.multiply(BigDecimal.valueOf(loanApplicationRequestDTO.getTerm())))
                .term(loanApplicationRequestDTO.getTerm())
                .monthlyPayment(monthlyPayment)
                .rate(totalRate)
                .isInsuranceEnabled(isInsuranceEnabled)
                .isSalaryClient(isSalaryClient)
                .build();

        log.debug("RETURNING loanOfferDTO, OUTPUT VALUES: {}", loanOfferDTO);
        return loanOfferDTO;
    }

    @Override
    public CreditDTO creditCalculation(ScoringDataDTO scoringDataDTO) throws ScoringException {

        BigDecimal currentRate  = scoringData(scoringDataDTO);
        log.trace("EXECUTION scoringData(). Returned currentRate value: {}", currentRate);

        BigDecimal monthlyPayment = ConveyorService.calculateMonthlyPayment(currentRate, scoringDataDTO.getTerm(), scoringDataDTO.getAmount());
        log.trace("EXECUTION calculateMonthlyPayment(). Returned monthlyPayment value: {}", monthlyPayment);

        BigDecimal psk = monthlyPayment.multiply(BigDecimal.valueOf(scoringDataDTO.getTerm()));

        CreditDTO creditDTO = CreditDTO.builder()
                .amount(scoringDataDTO.getAmount())
                .term(scoringDataDTO.getTerm())
                .monthlyPayment(monthlyPayment)
                .rate(currentRate)
                .psk(psk)
                .isInsuranceEnabled(scoringDataDTO.getIsInsuranceEnabled())
                .isSalaryClient(scoringDataDTO.getIsSalaryClient())
                .paymentSchedule(paymentScheduleInfo(scoringDataDTO, monthlyPayment, currentRate, psk))
                .build();

        log.debug("RETURNING creditDTO, OUTPUT VALUES: {}", creditDTO);
        return creditDTO;
    }

    private List<PaymentScheduleElementDTO> paymentScheduleInfo(ScoringDataDTO scoringDataDTO, BigDecimal monthlyPayment, BigDecimal currentRate, BigDecimal amount) {

        List<PaymentScheduleElementDTO> paymentScheduleElements = new ArrayList<>();

        BigDecimal totalPayment = zero;
        BigDecimal interestPayment = zero;
        BigDecimal debtPayment = zero;
        BigDecimal remainingDebt = scoringDataDTO.getAmount();

        for (int i = 0; i < scoringDataDTO.getTerm()+1; i++) {

            if (i == scoringDataDTO.getTerm()) {
                debtPayment = debtPayment.add(remainingDebt); // last payment
                remainingDebt = zero; // loan balance
            }

            paymentScheduleElements.add(PaymentScheduleElementDTO.builder()
                    .number(i)
                    .date(LocalDate.now().plusMonths(i))
                    .totalPayment(totalPayment.setScale(2, RoundingMode.HALF_UP))
                    .interestPayment(interestPayment.setScale(3, RoundingMode.HALF_UP))
                    .debtPayment(debtPayment.setScale(2, RoundingMode.HALF_UP))
                    .remainingDebt(remainingDebt.setScale(2, RoundingMode.HALF_UP))
                    .build());

                interestPayment = ConveyorService.calculatePercent(remainingDebt, currentRate); // percent
                debtPayment = monthlyPayment.subtract(interestPayment); // principal repayment
                totalPayment = totalPayment.add(monthlyPayment); // total monthly payment
                remainingDebt = remainingDebt.subtract(debtPayment); // loan balance
        }

        log.trace("RETURNING paymentScheduleElements List, size: {}", paymentScheduleElements.size());
        log.debug("RETURNING VALUES [paymentScheduleElements]: {}", paymentScheduleElements);
        return paymentScheduleElements;
    }

    private BigDecimal scoringData(ScoringDataDTO scoringDataDTO) throws ScoringException {

        BigDecimal currentRate = rate;
        log.trace("INITIALIZING currentRate from service.rate value: {}", rate);

        if (scoringDataDTO.getEmployment().getWorkExperienceCurrent() < 3) {
            log.debug("OFFER REJECTED. Caused by scoring constraint: current work experience < 3");
            throw new ScoringException("Current work experience < 3");
        }

        if (scoringDataDTO.getEmployment().getWorkExperienceTotal() < 12) {
            log.debug("OFFER REJECTED. Caused by scoring constraint: total work experience < 12");
            throw new ScoringException("Total work experience < 12");
        }

        if (scoringDataDTO.getAmount().compareTo(scoringDataDTO.getEmployment().getSalary()
                .multiply(BigDecimal.valueOf(20))) > 0) {
            log.debug("OFFER REJECTED. Caused by scoring constraint: 20 salaries < amount");
            throw new ScoringException("20 salaries < amount");
        }

        if (ConveyorService.calculateAge(scoringDataDTO.getBirthdate()) < 20 ||
                ConveyorService.calculateAge(scoringDataDTO.getBirthdate()) > 60) {
            log.debug("OFFER REJECTED. Caused by scoring constraint: 20 < age < 60");
            throw new ScoringException("20 < Age < 60");
        }

        switch (scoringDataDTO.getEmployment().getEmploymentStatus()) {

            case SELF_EMPLOYED: currentRate = currentRate.add(changeRateByOne);
                                log.trace("CHANGING RATE. Increasing by 1, caused by: EmploymentStatus " +
                                        "SELF_EMPLOYED, current value: {}", currentRate); break;

            case BUSINESS_OWNER: currentRate = currentRate.add(changeRateByOne);
                                 log.trace("CHANGING RATE. Increasing by 1, caused by: EmploymentStatus " +
                                         "BUSINESS_OWNER, current value: {}", currentRate); break;

            case UNEMPLOYED: log.debug("OFFER REJECTED. Caused by scoring constraint: employment status - UNEMPLOYED");
                             throw new ScoringException("Employment Status: UNEMPLOYED");
        }

        switch (scoringDataDTO.getEmployment().getPosition()) {

            case MIDDLE_MANAGER: currentRate = currentRate.subtract(changeRateByTwo);
                                 log.trace("CHANGING RATE. Subtracting by 2, caused by: Position " +
                                         "MIDDLE_MANAGER, current value: {}", currentRate); break;

            case TOP_MANAGER: currentRate = currentRate.subtract(changeRateByFour);
                              log.trace("CHANGING RATE. Subtracting by 4, caused by: Position " +
                                      "TOP_MANAGER, current value: {}", currentRate); break;

        }

        switch (scoringDataDTO.getMaritalStatus()) {

            case MARRIED: currentRate = currentRate.subtract(changeRateByThree);
                          log.trace("CHANGING RATE. Subtracting by 3, caused by: MaritalStatus " +
                                  "MARRIED, current value: {}", currentRate); break;

            case NOT_MARRIED: currentRate = currentRate.add(changeRateByTwo);
                              log.trace("CHANGING RATE. Increasing by 2, caused by: MaritalStatus " +
                                      "NOT_MARRIED, current value: {}", currentRate); break;

            case DIVORCED: currentRate = currentRate.add(changeRateByOne);
                           log.trace("CHANGING RATE. Increasing by 1, caused by: MaritalStatus " +
                                   "DIVORCED, current value: {}", currentRate); break;
        }

        switch (scoringDataDTO.getGender()) {

            case FEMALE:
                if (ConveyorService.calculateAge(scoringDataDTO.getBirthdate()) <= 60
                    && ConveyorService.calculateAge(scoringDataDTO.getBirthdate()) >= 35) {
                    currentRate = currentRate.subtract(changeRateByThree);
                    log.trace("CHANGING RATE. Subtracting by 3, caused by: Gender FEMALE, " +
                            "35 <= age <= 60, current value: {}", currentRate);} break;

            case MALE:
                if (ConveyorService.calculateAge(scoringDataDTO.getBirthdate()) <= 55
                    && ConveyorService.calculateAge(scoringDataDTO.getBirthdate()) >= 30) {
                    currentRate = currentRate.subtract(changeRateByThree);
                    log.trace("CHANGING RATE. Subtracting by 3, caused by: Gender MALE, " +
                            "30 <= age <= 55, current value: {}", currentRate);} break;

            case NON_BINARY:
                currentRate = currentRate.add(changeRateByThree);
                log.trace("CHANGING RATE. Increasing by 3, caused by: Gender NON_BINARY, " +
                         "current value: {}", currentRate); break;
        }

        if (scoringDataDTO.getDependentAmount() > 1) {
            currentRate = currentRate.add(changeRateByOne);
            log.trace("CHANGING RATE. Increasing by 1, caused by: DependentAmount > 1, " +
                    "current value {}", currentRate);
        }

        if (scoringDataDTO.getIsSalaryClient().equals(true)) {
            currentRate = currentRate.subtract(changeRateByOne);
            log.trace("CHANGING RATE. Subtracting by 1, caused by: IsSalaryClient TRUE, " +
                    "current value: {}", currentRate);
        }

        if (scoringDataDTO.getIsInsuranceEnabled().equals(true)) {
            currentRate = currentRate.subtract(changeRateByThree);
            log.trace("CHANGING RATE. Subtracting by 3, caused by: isInsuranceEnabled TRUE, current value: {}", currentRate);
            scoringDataDTO.setAmount(scoringDataDTO.getAmount().add(scoringDataDTO.getAmount().multiply(insurancePercent)));
            log.trace("CHANGING AMOUNT. Increasing by 5 percent, caused by: isInsuranceEnabled TRUE, current value: {}", scoringDataDTO.getAmount());
        }

        log.debug("RETURNING currentRate after scoring, value: {}", currentRate);
        return currentRate;
    }
}