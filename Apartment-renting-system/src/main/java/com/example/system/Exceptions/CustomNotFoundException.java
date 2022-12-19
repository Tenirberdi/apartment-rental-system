package com.example.system.exceptions;

public class CustomNotFoundException extends Exception {
    public CustomNotFoundException(String errorMessage){
        super(errorMessage);
    }
}
