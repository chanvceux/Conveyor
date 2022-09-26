package com.neoflex.conveyor.controller;

import com.neoflex.conveyor.dto.CreditDTO;
import com.neoflex.conveyor.dto.LoanApplicationRequestDTO;
import com.neoflex.conveyor.dto.LoanOfferDTO;
import com.neoflex.conveyor.dto.ScoringDataDTO;
import com.neoflex.conveyor.exception_handler.ScoringException;
import com.neoflex.conveyor.service.ConveyorServiceImpl;
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
    private ConveyorServiceImpl conveyorServices;

    @PostMapping("/conveyor/offers")
    @Operation(summary = "Loan offers", description = "Describes four personal approximate credit offers")
    public List<LoanOfferDTO> offers (@Valid @RequestBody LoanApplicationRequestDTO loanApplicationRequestDTO) {

            log.debug("GETTING LoanApplicationRequestDTO, INPUT VALUES: {}", loanApplicationRequestDTO);
            return conveyorServices.offers(loanApplicationRequestDTO);
    }

    @PostMapping("/conveyor/calculation")
    public CreditDTO calculation (@RequestBody ScoringDataDTO scoringDataDTO) throws ScoringException {

        log.debug("GETTING ScoringDataDTO, INPUT VALUES: {}", scoringDataDTO);
        return conveyorServices.creditCalculation(scoringDataDTO);
    }

}
