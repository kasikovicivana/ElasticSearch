package com.example.ddmdemo.exceptionhandling.exception;

public class LoadingException extends RuntimeException {

    public LoadingException(String message) {
        super(message);
    }
}
