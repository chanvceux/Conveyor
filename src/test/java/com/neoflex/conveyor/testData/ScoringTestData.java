package com.neoflex.conveyor.testData;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neoflex.conveyor.dto.ScoringDataDTO;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ScoringTestData {
    static String jsonPathScoringDataDTO = "./src/main/resources/json/ScoringData.json";
    static final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private static List<ScoringDataDTO> getScoringTestData() throws IOException {
         return objectMapper.readValue(new File(jsonPathScoringDataDTO), new TypeReference<>() {});
    }

    public static ScoringDataDTO getCorrectDataFirst() throws IOException {
        return getScoringTestData().get(0);
    }

    public static ScoringDataDTO getCorrectDataSecond() throws IOException {
        return getScoringTestData().get(1);
    }
    public static ScoringDataDTO getRejectedByWorkExperienceCurrent() throws IOException {
        return getScoringTestData().get(2);
    }

    public static ScoringDataDTO getRejectedByWorkExperienceTotal() throws IOException {
        return getScoringTestData().get(3);
    }

    public static ScoringDataDTO getRejectedBySalary() throws IOException {
        return getScoringTestData().get(4);
    }

    public static ScoringDataDTO getRejectedByAge() throws IOException {
        return getScoringTestData().get(5);
    }

    public static ScoringDataDTO getRejectedByUnemployedStatus() throws IOException {
        return getScoringTestData().get(6);
    }

}
