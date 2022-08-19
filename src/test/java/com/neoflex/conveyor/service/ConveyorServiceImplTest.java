package com.neoflex.conveyor.service;

import com.neoflex.conveyor.dto.*;
import com.neoflex.conveyor.enumeration.EmploymentStatus;
import com.neoflex.conveyor.enumeration.Gender;
import com.neoflex.conveyor.enumeration.MaritalStatus;
import com.neoflex.conveyor.enumeration.Position;
import com.neoflex.conveyor.exceptionHandler.ScoringException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
class ConveyorServiceImplTest {

    @Spy
    ConveyorServiceImpl conveyorServices = new ConveyorServiceImpl();

    @Test
    void offers() {

        ReflectionTestUtils.setField(conveyorServices, "rate", BigDecimal.valueOf(20));

        LoanApplicationRequestDTO loanApplicationRequestDTO = LoanApplicationRequestDTO.builder()
                .amount(BigDecimal.valueOf(20000))
                .term(6)
                .firstName("Valentina")
                .lastName("Tevyants")
                .middleName("Alexandrovna")
                .email("valentina.tev@yandex.ru")
                .birthdate(LocalDate.of(2002, 7, 1))
                .passportSeries("2016")
                .passportNumber("997783")
                .build();

        List<LoanOfferDTO> loanOfferDTOS = new ArrayList<>();

        LoanOfferDTO loanOfferDTOTest1 = buildLoanOfferDTO(BigDecimal.valueOf(20000),
                BigDecimal.valueOf(21182.40000).setScale(5),
                BigDecimal.valueOf(3530.40000).setScale(5),
                BigDecimal.valueOf(20),
                false, false);

        LoanOfferDTO loanOfferDTOTest2 = buildLoanOfferDTO(BigDecimal.valueOf(20000),
                BigDecimal.valueOf(21122.40000).setScale(5),
                BigDecimal.valueOf(3520.40000).setScale(5),
                BigDecimal.valueOf(19),
                false, true);

        LoanOfferDTO loanOfferDTOTest3 = buildLoanOfferDTO(BigDecimal.valueOf(21000.00).setScale(2),
                BigDecimal.valueOf(22053.7800000).setScale(7),
                BigDecimal.valueOf(3675.6300000).setScale(7),
                BigDecimal.valueOf(17),
                true, false);

        LoanOfferDTO loanOfferDTOTest4 = buildLoanOfferDTO(BigDecimal.valueOf(21000.00).setScale(2),
                BigDecimal.valueOf(21990.7800000).setScale(7),
                BigDecimal.valueOf(3665.1300000).setScale(7),
                BigDecimal.valueOf(16), true, true);

        loanOfferDTOS.add(loanOfferDTOTest1);
        loanOfferDTOS.add(loanOfferDTOTest2);
        loanOfferDTOS.add(loanOfferDTOTest3);
        loanOfferDTOS.add(loanOfferDTOTest4);

        Assertions.assertEquals(loanOfferDTOS, conveyorServices.offers(loanApplicationRequestDTO));

    }

    private LoanOfferDTO buildLoanOfferDTO(BigDecimal requestedAmount, BigDecimal totalAmount,
                                           BigDecimal monthlyPayment, BigDecimal rate,
                                           Boolean isInsuranceEnabled, Boolean isSalaryClient) {

        return LoanOfferDTO.builder()
                .applicationId(null)
                .requestedAmount(requestedAmount)
                .totalAmount(totalAmount)
                .term(6)
                .monthlyPayment(monthlyPayment)
                .rate(rate)
                .isInsuranceEnabled(isInsuranceEnabled)
                .isSalaryClient(isSalaryClient)
                .build();

    }

    @Test
    void creditCalculation() throws ScoringException {

        ReflectionTestUtils.setField(conveyorServices, "rate", BigDecimal.valueOf(20));

        List<PaymentScheduleElementDTO> paymentScheduleElementDTOSTest1 = new ArrayList<>();
        List<PaymentScheduleElementDTO> paymentScheduleElementDTOSTest2 = new ArrayList<>();

        paymentScheduleElementDTOSTest1.add(buildPaymentList(1,
                LocalDate.of(2022, 9, 14),
                BigDecimal.valueOf(106709.4000000).setScale(7),
                BigDecimal.valueOf(1709.4000000).setScale(7),
                BigDecimal.valueOf(105000.0000000).setScale(7),
                BigDecimal.valueOf(106709.4000000).setScale(7)));

        paymentScheduleElementDTOSTest1.add(buildPaymentList(2,
                LocalDate.of(2022, 10, 14),
                BigDecimal.valueOf(213418.8000000).setScale(7),
                BigDecimal.valueOf(1709.4000000).setScale(7),
                BigDecimal.valueOf(105000.0000000).setScale(7),
                BigDecimal.valueOf(0E-7).setScale(7)));

        paymentScheduleElementDTOSTest2.add(buildPaymentList(1,
                LocalDate.of(2022, 9, 14),
                BigDecimal.valueOf(102508.00000).setScale(5),
                BigDecimal.valueOf(2508.00000).setScale(5),
                BigDecimal.valueOf(100000.00000).setScale(5),
                BigDecimal.valueOf(102508.00000).setScale(5)));

        paymentScheduleElementDTOSTest2.add(buildPaymentList(2,
                LocalDate.of(2022, 10, 14),
                BigDecimal.valueOf(205016.00000).setScale(5),
                BigDecimal.valueOf(2508.00000).setScale(5),
                BigDecimal.valueOf(100000.00000).setScale(5),
                BigDecimal.valueOf(0.00000).setScale(5)));


        EmploymentDTO employmentDTOTest1 = buildEmploymentDTO(EmploymentStatus.BUSINESS_OWNER, BigDecimal.valueOf(30000),
                Position.TOP_MANAGER);

        EmploymentDTO employmentDTOTest2 = buildEmploymentDTO(EmploymentStatus.SELF_EMPLOYED, BigDecimal.valueOf(20000),
                Position.MIDDLE_MANAGER);


        ScoringDataDTO scoringDataDTOTest1 = buildScoringData(Gender.FEMALE, 0,
                employmentDTOTest1, MaritalStatus.MARRIED, true, false);

        ScoringDataDTO scoringDataDTOTest2 = buildScoringData(Gender.MALE, 2,
                employmentDTOTest2, MaritalStatus.DIVORCED, false, true);

        CreditDTO creditDTOTest1 = CreditDTO.builder()
                .amount (BigDecimal.valueOf(210000.00).setScale(2))
                .term(2)
                .monthlyPayment(BigDecimal.valueOf(106709.4000000).setScale(7))
                .rate(BigDecimal.valueOf(13))
                .psk(BigDecimal.valueOf(213418.8000000).setScale(7))
                .isInsuranceEnabled(true)
                .isSalaryClient(false)
                .paymentSchedule(paymentScheduleElementDTOSTest1)
                .build();

        CreditDTO creditDTOTest2 = CreditDTO.builder()
                .amount (BigDecimal.valueOf(200000))
                .term(2)
                .monthlyPayment(BigDecimal.valueOf(102508.00000).setScale(5))
                .rate(BigDecimal.valueOf(20))
                .psk(BigDecimal.valueOf(205016.00000).setScale(5))
                .isInsuranceEnabled(false)
                .isSalaryClient(true)
                .paymentSchedule(paymentScheduleElementDTOSTest2)
                .build();

        Assertions.assertEquals(creditDTOTest1, conveyorServices.creditCalculation(scoringDataDTOTest1));
        Assertions.assertEquals(creditDTOTest2, conveyorServices.creditCalculation(scoringDataDTOTest2));

    }

    private EmploymentDTO buildEmploymentDTO(EmploymentStatus employmentStatus, BigDecimal salary, Position position) {

        return EmploymentDTO.builder()
                .employmentStatus(employmentStatus)
                .employerINN("3434")
                .salary(salary)
                .position(position)
                .workExperienceTotal(12)
                .workExperienceCurrent(3)
                .build();
    }

    private PaymentScheduleElementDTO buildPaymentList(Integer number, LocalDate date, BigDecimal totalPayment,
                                                       BigDecimal interestPayment, BigDecimal debtPayment, BigDecimal remainingDebt) {

        return PaymentScheduleElementDTO.builder()
                .number(number)
                .date(date)
                .totalPayment(totalPayment)
                .interestPayment(interestPayment)
                .debtPayment(debtPayment)
                .remainingDebt(remainingDebt)
                .build();
    }

    private ScoringDataDTO buildScoringData(Gender gender, Integer dependentAmount,
                                            EmploymentDTO employment, MaritalStatus maritalStatus,
                                            Boolean isInsuranceEnabled, Boolean isSalaryClient) {

        return ScoringDataDTO.builder()
                .amount(BigDecimal.valueOf(200000))
                .term(2)
                .firstName("firstName")
                .lastName("lastName")
                .middleName("middleName")
                .gender(gender)
                .birthdate(LocalDate.of(2000, 7, 1))
                .passportSeries("2016")
                .passportNumber("234321")
                .passportIssueDate(LocalDate.of(2000, 7, 1))
                .passportIssueBranch("232")
                .maritalStatus(maritalStatus)
                .dependentAmount(dependentAmount)
                .employment(employment)
                .account("String")
                .isInsuranceEnabled(isInsuranceEnabled)
                .isSalaryClient(isSalaryClient)
                .build();
    }
}