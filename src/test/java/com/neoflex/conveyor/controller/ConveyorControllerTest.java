package com.neoflex.conveyor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neoflex.conveyor.dto.LoanApplicationRequestDTO;
import com.neoflex.conveyor.testData.LoanApplicationRequestTestData;
import com.neoflex.conveyor.testData.ScoringTestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class ConveyorControllerTest {

    @Autowired
    @MockBean
    ConveyorController conveyorController;
    @MockBean
    LoanApplicationRequestDTO loanApplicationRequestDTO;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Test
    void postConveyorOffersWrongFirstName() throws Exception {

        mockMvc.perform(post("/conveyor/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(LoanApplicationRequestTestData.getDataWithWrongFirstName())))
                .andExpect(status().isBadRequest());

        verify(conveyorController, times(0)).offers(loanApplicationRequestDTO);
    }

    @Test
    void postConveyorOffersWrongMiddleName() throws Exception {

        mockMvc.perform(post("/conveyor/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(LoanApplicationRequestTestData.getDataWithWrongMiddleName())))
                .andExpect(status().isBadRequest());

        verify(conveyorController, times(0)).offers(loanApplicationRequestDTO);
    }

    @Test
    void postConveyorOffersWrongLastName() throws Exception {

        mockMvc.perform(post("/conveyor/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(LoanApplicationRequestTestData.getDataWithWrongLastName())))
                .andExpect(status().isBadRequest());

        verify(conveyorController, times(0)).offers(loanApplicationRequestDTO);
    }
    @Test
    void postConveyorOffersWrongBirthDate() throws Exception {

        mockMvc.perform(post("/conveyor/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(LoanApplicationRequestTestData.getDataWithWrongBirthDate())))
                .andExpect(status().isBadRequest());

        verify(conveyorController, times(0)).offers(loanApplicationRequestDTO);
    }
    @Test
    void postConveyorOffersWrongTerm() throws Exception {

        mockMvc.perform(post("/conveyor/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(LoanApplicationRequestTestData.getDataWithWrongTerm())))
                .andExpect(status().isBadRequest());

        verify(conveyorController, times(0)).offers(loanApplicationRequestDTO);
    }

    @Test
    void postConveyorOffersWrongAmount() throws Exception {

        mockMvc.perform(post("/conveyor/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(LoanApplicationRequestTestData.getDataWithWrongAmount())))
                .andExpect(status().isBadRequest());

        verify(conveyorController, times(0)).offers(loanApplicationRequestDTO);
    }

    @Test
    void postConveyorOffersWrongEmail() throws Exception {

        mockMvc.perform(post("/conveyor/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(LoanApplicationRequestTestData.getDataWithWrongEmail())))
                .andExpect(status().isBadRequest());

        verify(conveyorController, times(0)).offers(loanApplicationRequestDTO);
    }

    @Test
    void postConveyorOffersWrongPassportSeries() throws Exception {

        mockMvc.perform(post("/conveyor/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(LoanApplicationRequestTestData.getDataWithWrongPassportSeries())))
                .andExpect(status().isBadRequest());

        verify(conveyorController, times(0)).offers(loanApplicationRequestDTO);
    }

    @Test
    void postConveyorOffersWrongPassportNumber() throws Exception {

        mockMvc.perform(post("/conveyor/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(LoanApplicationRequestTestData.getDataWithWrongPassportNumber())))
                .andExpect(status().isBadRequest());

        verify(conveyorController, times(0)).offers(loanApplicationRequestDTO);
    }

    @Test
    void offers() throws Exception {

        mockMvc.perform(post("/conveyor/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(LoanApplicationRequestTestData.getCorrectData())))
                .andExpect(status().isOk());
    }

    @Test
    void calculation() throws Exception {

        mockMvc.perform(post("/conveyor/calculation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ScoringTestData.getCorrectDataFirst())))
                .andExpect(status().isOk());
    }

}