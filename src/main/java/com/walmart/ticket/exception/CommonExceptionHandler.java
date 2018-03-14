package com.walmart.ticket.exception;

import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@ControllerAdvice(annotations = RestController.class)
@RequestMapping(produces = "application/vnd.error+json")
public class CommonExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity< VndErrors > validationException(final IllegalArgumentException e) {
        return error(e, HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
    }

    @ExceptionHandler(SeatHoldNotFoundException.class)
    public ResponseEntity < VndErrors > notFoundException(final SeatHoldNotFoundException e) {
        return error(e, HttpStatus.NOT_FOUND, e.getId().toString());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity < VndErrors > allOtherException(final RuntimeException e) {
        return error(e, HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
    }

    private ResponseEntity < VndErrors > error(final Exception exception, final HttpStatus httpStatus,
                                               final String logRef) {
        final String message = Optional.of(exception.getMessage())
                .orElse(exception.getClass().getSimpleName());
        return new ResponseEntity <> (new VndErrors(logRef, message), httpStatus);
    }
}
