package com.neoflex.conveyor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neoflex.conveyor.dto.EmploymentDTO;
import com.neoflex.conveyor.dto.LoanApplicationRequestDTO;
import com.neoflex.conveyor.dto.ScoringDataDTO;
import com.neoflex.conveyor.enumeration.EmploymentStatus;
import com.neoflex.conveyor.enumeration.Gender;
import com.neoflex.conveyor.enumeration.MaritalStatus;
import com.neoflex.conveyor.enumeration.Position;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class ConveyorControllerTest {

    @MockBean
    ConveyorController conveyorController;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void offers() throws Exception {

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

        mockMvc.perform(post("/conveyor/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanApplicationRequestDTO)))
                .andExpect(status().isOk());

    }

    @Test
    void calculation() throws Exception {

        EmploymentDTO employmentDTO = EmploymentDTO.builder()
                .employmentStatus(EmploymentStatus.BUSINESS_OWNER)
                .employerINN("3434")
                .salary(BigDecimal.valueOf(30000))
                .position(Position.TOP_MANAGER)
                .workExperienceTotal(12)
                .workExperienceCurrent(3)
                .build();

        ScoringDataDTO scoringDataDTO = ScoringDataDTO.builder()
                .amount(BigDecimal.valueOf(200000))
                .term(2)
                .firstName("firstName")
                .lastName("lastName")
                .middleName("middleName")
                .gender(Gender.FEMALE)
                .birthdate(LocalDate.of(2000, 7, 1))
                .passportSeries("2016")
                .passportNumber("234321")
                .passportIssueDate(LocalDate.of(2000, 7, 1))
                .passportIssueBranch("232")
                .maritalStatus(MaritalStatus.MARRIED)
                .dependentAmount(0)
                .employment(employmentDTO)
                .account("String")
                .isInsuranceEnabled(true)
                .isSalaryClient(false)
                .build();

        mockMvc.perform(post("/conveyor/calculation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scoringDataDTO)))
                .andExpect(status().isOk());

    }
}