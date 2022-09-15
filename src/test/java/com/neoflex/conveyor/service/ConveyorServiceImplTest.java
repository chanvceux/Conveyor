package com.neoflex.conveyor.service;

import com.neoflex.conveyor.exceptionHandler.ScoringException;
import com.neoflex.conveyor.testData.CreditTestData;
import com.neoflex.conveyor.testData.LoanApplicationRequestTestData;
import com.neoflex.conveyor.testData.LoanOfferTestData;
import com.neoflex.conveyor.testData.ScoringTestData;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(MockitoJUnitRunner.class)
@EnableWebMvc
@SpringBootTest
@AutoConfigureMockMvc

class ConveyorServiceImplTest {

    @Autowired
    ConveyorServiceImpl conveyorServices = new ConveyorServiceImpl();
    @Test
    void offers() throws IOException {
      assertEquals(LoanOfferTestData.getLoanOfferTestData(),
              conveyorServices.offers(LoanApplicationRequestTestData.getCorrectData()));
    }

    @Test
    void creditCalculation() throws ScoringException, IOException {
        assertEquals(CreditTestData.getFirstCorrectData(),
                conveyorServices.creditCalculation(ScoringTestData.getCorrectDataFirst()));
        assertEquals(CreditTestData.getSecondCorrectData(),
                conveyorServices.creditCalculation(ScoringTestData.getCorrectDataSecond()));

    }

    @Test
    void scoringRejectedByWorkExperienceCurrent() {

        ScoringException thrown = assertThrows(ScoringException.class, () -> {
            conveyorServices.creditCalculation(ScoringTestData.getRejectedByWorkExperienceCurrent());
        });
        assertEquals(thrown.getMessage(), "Current work experience < 3");
    }

    @Test
    void scoringRejectedByWorkExperienceTotal() {

        ScoringException thrown = assertThrows(ScoringException.class, () -> {
            conveyorServices.creditCalculation(ScoringTestData.getRejectedByWorkExperienceTotal());
        });
        assertEquals(thrown.getMessage(), "Total work experience < 12");
    }

    @Test
    void scoringRejectedBySalary() {

        ScoringException thrown = assertThrows(ScoringException.class, () -> {
            conveyorServices.creditCalculation(ScoringTestData.getRejectedBySalary());
        });
        assertEquals(thrown.getMessage(), "20 salaries < amount");
    }

    @Test
    void scoringRejectedByAge() {

        ScoringException thrown = assertThrows(ScoringException.class, () -> {
            conveyorServices.creditCalculation(ScoringTestData.getRejectedByAge());
        });
        assertEquals(thrown.getMessage(), "20 < Age < 60");
    }

    @Test
    void scoringRejectedByUnemployedStatus() {

        ScoringException thrown = assertThrows(ScoringException.class, () -> {
            conveyorServices.creditCalculation(ScoringTestData.getRejectedByUnemployedStatus());
        });
        assertEquals(thrown.getMessage(), "Employment Status: UNEMPLOYED");
    }



}