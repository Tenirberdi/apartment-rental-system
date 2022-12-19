package com.example.system.errorhandlers;

import com.example.system.errorhandlers.interfaces.ControllerAdvice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class ExceptionResolver extends Exception {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException e){
        return ResponseEntity.status(403).body(e.getMessage());
    }
}
