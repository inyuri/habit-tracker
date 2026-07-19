package com.timofey.habit_tracker.handler;

import com.timofey.habit_tracker.exception.HabitNotFoundException;
import com.timofey.habit_tracker.exception.RecordAlreadyExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HabitNotFoundException.class)
    public ResponseEntity<Map<String, Object>> habitNotFound(HabitNotFoundException ex) {
        log.warn("Habit not found: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "status", HttpStatus.NOT_FOUND.value(),
                             "message", ex.getMessage())
                );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> badCredentials(BadCredentialsException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "status", HttpStatus.UNAUTHORIZED.value(),
                        "message", "Invalid username or password"
                ));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> dataIntegrityViolation(DataIntegrityViolationException ex) {

        log.warn("Database constraint violation: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "status", HttpStatus.CONFLICT.value(),
                        "message", "Database constraint violation"
                ));
    }

    @ExceptionHandler(RecordAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> recordAlreadyExists(RecordAlreadyExistsException ex) {
        log.warn("Record already exists: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "status", HttpStatus.CONFLICT.value(),
                        "message", ex.getMessage()
                ));
    }
}
