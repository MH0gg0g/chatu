package com.example.chatu.secuirty;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageExceptionHandler(MethodArgumentNotValidException.class)
    public void handleValidation(MethodArgumentNotValidException ex, Principal principal) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errors.put(fe.getField(), fe.getDefaultMessage());
        }
        if (principal != null) {
            messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/errors", errors);
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        var errors = new HashMap<String, String>();
        for (var fe : ex.getBindingResult().getFieldErrors()) {
            errors.put(fe.getField(), fe.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(Map.of("errors", errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralErrors(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", ex.getMessage()));
    }
}
