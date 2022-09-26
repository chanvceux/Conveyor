package com.neoflex.conveyor.exception_handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ScoringExceptionHandler {

    @ExceptionHandler(ScoringException.class)
    public ResponseEntity<Response> handleException(ScoringException e) {

        Response response = new Response(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    //todo
}
