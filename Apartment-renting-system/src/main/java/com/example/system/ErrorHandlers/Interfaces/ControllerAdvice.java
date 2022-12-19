package com.example.system.errorhandlers.interfaces;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

// ControllerAdvice annotation
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface ControllerAdvice {

}
