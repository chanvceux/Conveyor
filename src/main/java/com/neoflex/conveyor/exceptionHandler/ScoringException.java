package com.neoflex.conveyor.exceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus (code = HttpStatus.BAD_REQUEST)
public class ScoringException extends Exception {

    public ScoringException(String message) {
        super(message);
    }

}
