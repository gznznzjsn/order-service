package com.gznznzjsn.orderservice.web.controller;


import com.gznznzjsn.orderservice.domain.exception.IllegalActionException;
import com.gznznzjsn.orderservice.domain.exception.NotEnoughResourcesException;
import com.gznznzjsn.orderservice.domain.exception.ResourceNotFoundException;
import com.gznznzjsn.orderservice.domain.exception.UniqueResourceException;
import com.gznznzjsn.orderservice.web.dto.ExceptionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDto handleResourceNotFound(
            ResourceNotFoundException e) {
        log.error(e.getMessage());
        e.printStackTrace();
        return ExceptionDto.builder()
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(NotEnoughResourcesException.class)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ExceptionDto handleNotEnoughResources(
            ResourceNotFoundException e) {
        log.error(e.getMessage());
        e.printStackTrace();
        return ExceptionDto.builder()
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(UniqueResourceException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionDto handleUniqueResource(
            UniqueResourceException e) {
        log.error(e.getMessage());
        e.printStackTrace();
        return ExceptionDto.builder()
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(IllegalActionException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ExceptionDto handleIllegalAction(
            IllegalActionException e) {
        log.error(e.getMessage());
        e.printStackTrace();
        return ExceptionDto.builder()
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler({AccessDeniedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ExceptionDto handleAccessDenied(AccessDeniedException e) {
        log.error(e.getMessage());
        e.printStackTrace();
        return ExceptionDto.builder()
                .message("Access denied!")
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        log.error(e.getMessage());
        e.printStackTrace();
        Map<String, String> otherInfo = e.getBindingResult()
                .getFieldErrors().stream()
                .collect(Collectors.toMap(
                                (FieldError::getField),
                                (fieldError ->
                                        fieldError.getDefaultMessage() == null ? "No message" : fieldError.getDefaultMessage()
                                )
                        )
                );
        return ExceptionDto.builder()
                .message("One or more of arguments are invalid!")
                .otherInfo(otherInfo)
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionDto handleOther(Exception e) {
        log.error(e.getMessage());
        e.printStackTrace();
        return ExceptionDto.builder()
                .message("Please, try later!")
                .build();
    }

}
