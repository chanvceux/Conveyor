package com.neoflex.conveyor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neoflex.conveyor.dto.LoanApplicationRequestDTO;
import com.neoflex.conveyor.test_data.LoanApplicationRequestTestData;
import com.neoflex.conveyor.test_data.ScoringTestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

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