package com.mj.mysns.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(exception.getMessage());
    }

    @ExceptionHandler(DuplicatedUsernameException.class)
    public ResponseEntity<String> handleDuplicatedUsernameException(
        DuplicatedUsernameException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(exception.getMessage());
    }

    @ExceptionHandler(DuplicatedUserException.class)
    public ResponseEntity<String> handleDataIntegrityViolationException(
        DuplicatedUserException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(exception.getMessage());
    }
}
