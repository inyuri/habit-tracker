package com.timofey.habit_tracker.controller;

import com.timofey.habit_tracker.dto.AuthResponse;
import com.timofey.habit_tracker.dto.LoginRequest;
import com.timofey.habit_tracker.dto.RegisterRequest;
import com.timofey.habit_tracker.dto.UsernameResponse;
import com.timofey.habit_tracker.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UsernameResponse> register(@RequestBody RegisterRequest registerRequest) {
        log.info("POST /api/auth/register");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        log.info("POST /api/auth/login");

        return ResponseEntity.ok(userService.login(loginRequest));
    }

}
