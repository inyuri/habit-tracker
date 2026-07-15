package com.timofey.habit_tracker.service;

import com.timofey.habit_tracker.dto.AuthResponse;
import com.timofey.habit_tracker.dto.LoginRequest;
import com.timofey.habit_tracker.dto.RegisterRequest;
import com.timofey.habit_tracker.dto.UsernameResponse;
import com.timofey.habit_tracker.model.User;
import com.timofey.habit_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UsernameResponse register(RegisterRequest registerRequest) {
        log.info("Register user with username={}, email={}",
                registerRequest.getUsername(),
                registerRequest.getEmail());

        User user = new User(registerRequest.getUsername(),
                passwordEncoder.encode(registerRequest.getPassword()),
                registerRequest.getEmail());

        User savedUser = userRepository.save(user);

        log.info("User register successfully with id={}", savedUser.getId());

        return new UsernameResponse(savedUser.getUsername());
    }

    public AuthResponse login(LoginRequest loginRequest) {
        log.info("Login user with username={}",
                loginRequest.getUsername());

        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        String token = jwtService.generateToken(user.getUsername());

        log.info("User login successfully with username={}",
                user.getUsername());

        return new AuthResponse(token, user.getUsername());
    }
}
