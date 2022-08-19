package com.neoflex.conveyor.service;

import com.neoflex.conveyor.dto.*;
import com.neoflex.conveyor.exceptionHandler.ScoringException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
public class ConveyorServiceImpl implements ConveyorService {

    @Value("${service.rate}")
    private static BigDecimal rate;

    @Value("${service.insurancePercent}")
    private static BigDecimal insurancePercent;

    @Value("${service.changeRateByOne}")
    private static BigDecimal changeRateByOne;

    @Value("${service.changeRateByTwo}")
    private static BigDecimal changeRateByTwo;

    @Value("${service.changeRateByThree}")
    private static BigDecimal changeRateByThree;

    @Value("${service.changeRateByFour}")
    private static BigDecimal changeRateByFour;

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

        CreditDTO creditDTO = CreditDTO.builder()
                .amount(scoringDataDTO.getAmount())
                .term(scoringDataDTO.getTerm())
                .monthlyPayment(monthlyPayment)
                .rate(currentRate)
                .psk(monthlyPayment.multiply(BigDecimal.valueOf(scoringDataDTO.getTerm())))
                .isInsuranceEnabled(scoringDataDTO.getIsInsuranceEnabled())
                .isSalaryClient(scoringDataDTO.getIsSalaryClient())
                .paymentSchedule(paymentScheduleInfo(scoringDataDTO, monthlyPayment))
                .build();

        log.debug("RETURNING creditDTO, OUTPUT VALUES: {}", creditDTO);
        return creditDTO;
    }

    private List<PaymentScheduleElementDTO> paymentScheduleInfo(ScoringDataDTO scoringDataDTO, BigDecimal monthlyPayment) {

        List<PaymentScheduleElementDTO> paymentScheduleElements = new ArrayList<>();

        BigDecimal percent = monthlyPayment.subtract(scoringDataDTO.getAmount()
                .divide(BigDecimal.valueOf(scoringDataDTO.getTerm()), RoundingMode.HALF_DOWN)); //Todo
        log.trace("CALCULATING percent, value: {}", percent);

        BigDecimal totalPayment = monthlyPayment;
        BigDecimal remainingDebt = monthlyPayment.multiply(BigDecimal.valueOf(scoringDataDTO.getTerm()))
                .subtract(monthlyPayment);
        log.trace("CALCULATING remainingDebt, value: {}", remainingDebt);

        for (int i = 1; i < scoringDataDTO.getTerm() + 1; i++) {

            paymentScheduleElements.add(PaymentScheduleElementDTO.builder()
                    .number(i)
                    .date(LocalDate.now().plusMonths(i))
                    .totalPayment(totalPayment)
                    .interestPayment(percent)
                    .debtPayment(monthlyPayment.subtract(percent))
                    .remainingDebt(remainingDebt)
                    .build());

            totalPayment = totalPayment.add(monthlyPayment);
            remainingDebt = remainingDebt.subtract(monthlyPayment);
        }

        log.debug("RETURNING paymentScheduleElements List, size: {}", paymentScheduleElements.size());
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