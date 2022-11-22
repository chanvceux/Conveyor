package com.neoflex.conveyor.service;

import com.neoflex.conveyor.exception_handler.ScoringException;
import com.neoflex.conveyor.test_data.LoanApplicationRequestTestData;
import com.neoflex.conveyor.test_data.LoanOfferTestData;
import com.neoflex.conveyor.test_data.ScoringTestData;
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
    void scoringRejectedByWorkExperienceCurrent() {

        ScoringException thrown = assertThrows(ScoringException.class, () -> {
            conveyorServices.creditCalculation(ScoringTestData.getRejectedByWorkExperienceCurrent());
        });
        assertEquals("Current work experience < 3", thrown.getMessage());
    }

    @Test
    void scoringRejectedByWorkExperienceTotal() {

        ScoringException thrown = assertThrows(ScoringException.class, () -> {
            conveyorServices.creditCalculation(ScoringTestData.getRejectedByWorkExperienceTotal());
        });
        assertEquals("Total work experience < 12", thrown.getMessage());
    }

    @Test
    void scoringRejectedBySalary() {

        ScoringException thrown = assertThrows(ScoringException.class, () -> {
            conveyorServices.creditCalculation(ScoringTestData.getRejectedBySalary());
        });
        assertEquals("20 salaries < amount",  thrown.getMessage());
    }

    @Test
    void scoringRejectedByAge() {

        ScoringException thrown = assertThrows(ScoringException.class, () -> {
            conveyorServices.creditCalculation(ScoringTestData.getRejectedByAge());
        });
        assertEquals("20 < Age < 60", thrown.getMessage());
    }

    @Test
    void scoringRejectedByUnemployedStatus() {

        ScoringException thrown = assertThrows(ScoringException.class, () -> {
            conveyorServices.creditCalculation(ScoringTestData.getRejectedByUnemployedStatus());
        });
        assertEquals("Employment Status: UNEMPLOYED", thrown.getMessage());
    }



}