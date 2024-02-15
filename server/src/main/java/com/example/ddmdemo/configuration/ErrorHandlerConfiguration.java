package com.example.ddmdemo.configuration;

import com.example.ddmdemo.exceptionhandling.ErrorObject;
import com.example.ddmdemo.exceptionhandling.exception.LoadingException;
import com.example.ddmdemo.exceptionhandling.exception.MalformedQueryException;
import com.example.ddmdemo.exceptionhandling.exception.NotFoundException;
import com.example.ddmdemo.exceptionhandling.exception.StorageException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ErrorHandlerConfiguration {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(StorageException.class)
    @ResponseBody
    ErrorObject handleStorageException(HttpServletRequest request, StorageException ex) {
        return new ErrorObject(request, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(LoadingException.class)
    @ResponseBody
    ErrorObject handleIdempotencyException(HttpServletRequest request, LoadingException ex) {
        return new ErrorObject(request, ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    ErrorObject handleIllegalArgumentException(HttpServletRequest request,
                                               IllegalArgumentException ex) {
        return new ErrorObject(request, ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    @ResponseBody
    ErrorObject handleNotFoundException(HttpServletRequest request, NotFoundException ex) {
        return new ErrorObject(request, ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseBody
    ErrorObject handleEntityNotFoundException(HttpServletRequest request,
                                              EntityNotFoundException ex) {
        return new ErrorObject(request, ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(MalformedQueryException.class)
    @ResponseBody
    ErrorObject handleMalformedQueryException(HttpServletRequest request,
                                              MalformedQueryException ex) {
        return new ErrorObject(request, ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}
