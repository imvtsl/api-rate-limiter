package com.vatsal.project.apiratelimiter.exceptionhandler;

import com.vatsal.project.apiratelimiter.dto.Violation;
import com.vatsal.project.apiratelimiter.dto.Violations;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;


/**
 * A global exception handler to handle exceptions for all controllers in this application.
 * Extends ResponseEntityExceptionHandler to reuse predefined spring MVC exception handling.
 * @author imvtsl
 * @since v1.0
 */

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    /**
     * Handles MethodArgumentNotValidException. Returns details for invalid arguments with HTTP status 400 Bad Request.
     * @param exception MethodArgumentNotValidException
     * @param headers
     * @param status
     * @param request
     * @return Violations
     */
    @Override
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<Violation> violationList = new ArrayList<>();
        exception.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            violationList.add(new Violation(fieldName, errorMessage));
        });
        return ResponseEntity.badRequest().body(new Violations(violationList));
    }

    /**
     * Handles MissingPathVariableException. Returns missing path variable details with HTTP status 400 Bad Request.
     * @param exception MissingPathVariableException
     * @return Violations
     */
    @Override
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException exception, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<Violation> violationList = new ArrayList<>();
        violationList.add(new Violation(exception.getVariableName(), exception.getMessage()));
        return ResponseEntity.badRequest().body(new Violations(violationList));
    }

    /**
     * A generic handler for all errors and exceptions whose handling is not defined.
     * @param exception ConstraintViolationException
     * @return Violation
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Violations> handleAllUncaughtExceptions(ConstraintViolationException exception) {
        List<Violation> violationList = new ArrayList<>();
        exception.getConstraintViolations().forEach(e -> {
            String fieldName = (String) e.getInvalidValue();
            String errorMessage = e.getMessage();
            violationList.add(new Violation(fieldName, errorMessage));
        });
        return ResponseEntity.badRequest().body(new Violations(violationList));
    }

    /**
     * A generic handler for all errors and exceptions whose handling is not defined.
     * @param throwable Throwable
     * @return Violation
     */
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Violation> handleAllUncaughtExceptions(Throwable throwable) {
        log.error(ExceptionUtils.getStackTrace(throwable));
        return ResponseEntity.internalServerError().body(new Violation("Please check logs for more details"));
    }
}
