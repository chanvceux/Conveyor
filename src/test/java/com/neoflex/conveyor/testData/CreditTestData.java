package com.neoflex.conveyor.testData;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neoflex.conveyor.dto.CreditDTO;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CreditTestData {

    static String jsonPathCreditDTO = "./src/main/resources/json/Credit.json";
    static final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private static List<CreditDTO> getCreditTestData() throws IOException {
        return objectMapper.readValue(new File(jsonPathCreditDTO), new TypeReference<>() {});
    }

    public static CreditDTO getFirstCorrectData() throws IOException {
        return getCreditTestData().get(0);
    }

    public static CreditDTO getSecondCorrectData() throws IOException {
        return getCreditTestData().get(1);
    }

}
