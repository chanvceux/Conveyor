package com.neoflex.conveyor.test_data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neoflex.conveyor.dto.LoanApplicationRequestDTO;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class LoanApplicationRequestTestData {

    static String jsonPathLoanAppReqDTO = "./src/test/resources/json/LoanApplicationRequest.json";
    static final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private static List<LoanApplicationRequestDTO> getLoanAppReqTestData() throws IOException {
        return objectMapper.readValue(new File(jsonPathLoanAppReqDTO), new TypeReference<>() {});
    }

    public static LoanApplicationRequestDTO getDataWithWrongFirstName() throws IOException {
        return getLoanAppReqTestData().get(0);
    }

    public static LoanApplicationRequestDTO getDataWithWrongMiddleName() throws IOException {
        return getLoanAppReqTestData().get(6);
    }

    public static LoanApplicationRequestDTO getDataWithWrongLastName() throws IOException {
        return getLoanAppReqTestData().get(5);
    }

    public static LoanApplicationRequestDTO getDataWithWrongBirthDate() throws IOException {
        return getLoanAppReqTestData().get(2);
    }

    public static LoanApplicationRequestDTO getDataWithWrongTerm() throws IOException {
        return getLoanAppReqTestData().get(4);
    }

    public static LoanApplicationRequestDTO getDataWithWrongAmount() throws IOException {
        return getLoanAppReqTestData().get(1);
    }

    public static LoanApplicationRequestDTO getDataWithWrongEmail() throws IOException {
        return getLoanAppReqTestData().get(7);
    }

    public static LoanApplicationRequestDTO getDataWithWrongPassportSeries() throws IOException {
        return getLoanAppReqTestData().get(8);
    }

    public static LoanApplicationRequestDTO getDataWithWrongPassportNumber() throws IOException {
        return getLoanAppReqTestData().get(9);
    }

    public static LoanApplicationRequestDTO getCorrectData() throws IOException {
        return getLoanAppReqTestData().get(3);
    }

}
