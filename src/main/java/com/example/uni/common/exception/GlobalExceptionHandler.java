package com.example.uni.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import jakarta.persistence.OptimisticLockException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<?> handle(ApiException e){
        return ResponseEntity.status(e.getCode().status)
                .body(Map.of(
                        "error", e.getCode().name(),
                        "message", e.getCode().message
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleBind(MethodArgumentNotValidException ex){
        var details = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> fe.getDefaultMessage() == null ? "invalid" : fe.getDefaultMessage(),
                        (a,b) -> a
                ));
        return ResponseEntity.badRequest()
                .body(Map.of("error", "VALIDATION_ERROR", "details", details));
    }

    @ExceptionHandler({ObjectOptimisticLockingFailureException.class, OptimisticLockException.class})
    public ResponseEntity<?> handleOptimistic(Exception ex){
        return ResponseEntity.status(ErrorCode.CONFLICT.status)
                .body(Map.of(
                        "error", "CONFLICT",
                        "message", "다시 시도해 주세요."
                ));
    }
}