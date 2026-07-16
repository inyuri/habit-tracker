package com.timofey.habit_tracker.service;

import com.timofey.habit_tracker.dto.AuthResponse;
import com.timofey.habit_tracker.dto.LoginRequest;
import com.timofey.habit_tracker.dto.RegisterRequest;
import com.timofey.habit_tracker.dto.UsernameResponse;
import com.timofey.habit_tracker.model.User;
import com.timofey.habit_tracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceMockitoTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldRegisterUser() {
        RegisterRequest registerRequest = new RegisterRequest(
                "alice",
                "alice123",
                "alice@gmail.com"
        );

        when(passwordEncoder.encode("alice123")).thenReturn("hashedPassword");

        User savedUser = new User(
                "alice",
                "hashedPassword",
                "alice@gmail.com"
        );
        savedUser.setId(1L);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UsernameResponse usernameResponse = userService.register(registerRequest);

        assertEquals("alice", usernameResponse.getUsername());

        verify(passwordEncoder).encode("alice123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldLoginUser() {
        LoginRequest loginRequest = new LoginRequest(
                "alice",
                "alice123"
        );

        User user = new User(
                "alice",
                "hashedPassword",
                "alice@gmail.com"
        );

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("alice123", "hashedPassword"))
                .thenReturn(true);
        when(jwtService.generateToken("alice"))
                .thenReturn("jwt-token");

        AuthResponse response = userService.login(loginRequest);

        assertEquals("jwt-token", response.getToken());
        assertEquals("alice", response.getUsername());

        verify(userRepository).findByUsername("alice");
        verify(passwordEncoder).matches("alice123", "hashedPassword");
        verify(jwtService).generateToken("alice");
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsIncorrect() {
        LoginRequest loginRequest = new LoginRequest(
                "alice",
                "alice1234"
        );

        User user = new User(
                "alice",
                "hashedPassword",
                "alice@gmail.com"
        );

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("alice1234", "hashedPassword"))
                .thenReturn(false);

        assertThrows(
                BadCredentialsException.class,
                () -> userService.login(loginRequest)
        );

        verify(userRepository).findByUsername("alice");
        verify(passwordEncoder).matches("alice1234", "hashedPassword");
        verify(jwtService, never()).generateToken("alice");
    }
}
