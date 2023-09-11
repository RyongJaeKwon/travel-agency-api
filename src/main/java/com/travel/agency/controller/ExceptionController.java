package com.travel.agency.controller;

import com.travel.agency.dto.response.ErrorResponse;
import com.travel.agency.exception.TravelApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException", e);

        List<FieldError> fieldErrors = e.getFieldErrors();
        Map<String, String> validation = fieldErrors.stream()
                .collect(Collectors.toMap(FieldError::getField, DefaultMessageSourceResolvable::getDefaultMessage, (message1, message2) -> {
                    return message1 + ", " + message2;
                }));

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(HttpStatus.BAD_REQUEST.toString())
                .message("잘못된 요청입니다")
                .validation(validation)
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    @ExceptionHandler(TravelApiException.class)
    public ResponseEntity<ErrorResponse> travelApiException(TravelApiException e) {
        log.error("TravelApiException", e);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(e.getStatusCode().toString())
                .message(e.getMessage())
                .validation(e.getValidation())
                .build();

        return ResponseEntity
                .status(e.getStatusCode())
                .body(errorResponse);
    }

}
