package ru.ulstu.reportservice.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(ResourceAccessException.class)
    public ProblemDetail handleFileStorageUnavailable(ResourceAccessException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE,
                "Файловое хранилище временно недоступно, попробуйте позже");
    }
}
