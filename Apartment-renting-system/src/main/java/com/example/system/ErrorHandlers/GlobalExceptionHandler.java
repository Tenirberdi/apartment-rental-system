package com.example.system.errorhandlers;

import com.example.system.errorhandlers.interfaces.RestControllerAdvice;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ConstraintViolationException;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler extends Exception {


    private Map<String, List<String>> getErrorsMap(List<String> errors) {
        Map<String, List<String>> errorResponse = new HashMap<>();
        errorResponse.put("errors", errors);
        return errorResponse;
    }
    private Map<String, List<Map<String, String>>> getErrorsMap(Map<String, String> errors) {
        Map<String, List<Map<String, String>>> errorResponse = new HashMap<>();
        errorResponse.put("errors", Arrays.asList(errors));
        return errorResponse;
    }

    @ExceptionHandler(ResponseStatusException.class)
    public final ResponseEntity<Map<String, List<String>>> handleResponseStatusExceptions(ResponseStatusException ex) {
        List<String> errors = Collections.singletonList(ex.getReason());
        return new ResponseEntity<>(getErrorsMap(errors), new HttpHeaders(), ex.getStatus());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public final ResponseEntity<?> handleValidationExceptions(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(v -> { errors.put(v.getPropertyPath().toString().trim().replaceAll("(.*)\\.", ""), v.getMessage());});
        return new ResponseEntity<>(getErrorsMap(errors),  HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> handleValidationErrors(MethodArgumentNotValidException ex) {
    //        Map<Object, Object> errors = ex.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getField));
    //
    //        Map<String, Map<Object, Object>> errorResponse = new HashMap<>();
    //        errorResponse.put("errors", errors);

        return new ResponseEntity<>(getErrorsMap(Arrays.asList("SOmthing")), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IndexOutOfBoundsException.class)
    public final ResponseEntity<Map<String, List<String>>> handleIndexOutOfBoundsExceptions(IndexOutOfBoundsException ex) {
        List<String> errors = Collections.singletonList(ex.getMessage());
        return new ResponseEntity<>(getErrorsMap(errors), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public final ResponseEntity<Map<String, List<String>>> handleRuntimeExceptions(RuntimeException ex) {
        List<String> errors = Collections.singletonList(ex.getMessage());
        return new ResponseEntity<>(getErrorsMap(errors), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Map<String, List<String>>> handleGeneralExceptions(Exception ex) {
        List<String> errors = Collections.singletonList(ex.getMessage());
        return new ResponseEntity<>(getErrorsMap(errors), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
