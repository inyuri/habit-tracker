package com.timofey.habit_tracker.controller;

import com.timofey.habit_tracker.dto.AuthResponse;
import com.timofey.habit_tracker.dto.LoginRequest;
import com.timofey.habit_tracker.dto.RegisterRequest;
import com.timofey.habit_tracker.dto.UsernameResponse;
import com.timofey.habit_tracker.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@Tag(
        name = "Authentication",
        description = "Регистрация и авторизация пользователей"
)
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    @Operation(
            summary = "Регистрация пользователя",
            description = "Создает нового пользователя и сохраняет его в системе"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Пользователь успешно зарегистрирован"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректные данные пользователя"
            )
    })
    public ResponseEntity<UsernameResponse> register(@RequestBody @Valid RegisterRequest registerRequest) {
        log.info("POST /api/auth/register");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.register(registerRequest));
    }

    @PostMapping("/login")
    @Operation(
            summary = "Авторизация пользователя",
            description = "Проверяет логин и пароль и возвращает JWT токен"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешная авторизация"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Неверный логин или пароль"
            )
    })
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        log.info("POST /api/auth/login");

        return ResponseEntity.ok(userService.login(loginRequest));
    }

}
