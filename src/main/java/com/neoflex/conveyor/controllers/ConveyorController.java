package com.neoflex.conveyor.controllers;

import com.neoflex.conveyor.dtos.CreditDTO;
import com.neoflex.conveyor.dtos.LoanApplicationRequestDTO;
import com.neoflex.conveyor.dtos.LoanOfferDTO;
import com.neoflex.conveyor.dtos.ScoringDataDTO;
import com.neoflex.conveyor.exceptionHandlers.ScoringException;
import com.neoflex.conveyor.services.ConveyorServicesImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
public class ConveyorController {

    @Autowired
    private ConveyorServicesImpl conveyorServices;

    @PostMapping("/conveyor/offers")
    @Operation(summary = "Loan offers", description = "Describes four personal approximate credit offers")
    public List<LoanOfferDTO> offers (@Valid @RequestBody LoanApplicationRequestDTO loanApplicationRequestDTO) {

        log.info("GETTING LoanApplicationRequestDTO, INPUT VALUES: {}", loanApplicationRequestDTO);
        log.info("EXECUTING ConveyorServicesImpl.offers()");
        return conveyorServices.offers(loanApplicationRequestDTO);
    }

    @PostMapping("/conveyor/calculation")
    public CreditDTO calculation (@RequestBody ScoringDataDTO scoringDataDTO) throws ScoringException {

        log.info("GETTING ScoringDataDTO, INPUT VALUES: {}", scoringDataDTO);
        log.info("EXECUTING ConveyorServicesImpl.creditCalculation()");
        return conveyorServices.creditCalculation(scoringDataDTO);
    }

}
