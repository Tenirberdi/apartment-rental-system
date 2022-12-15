package com.example.system.Exceptions;

public class CustomNotFoundException extends Exception {
    public CustomNotFoundException(String errorMessage){
        super(errorMessage);
    }
}
