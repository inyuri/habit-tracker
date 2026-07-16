package com.timofey.habit_tracker.service;

import com.timofey.habit_tracker.dto.*;
import com.timofey.habit_tracker.model.User;
import com.timofey.habit_tracker.repository.HabitRepository;
import com.timofey.habit_tracker.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private HabitRepository habitRepository;

    @AfterEach
    void cleanDatabase() {
        habitRepository.deleteAll();
        userRepository.deleteAll();
    }

    private RegisterRequest createRegisterRequest() {
        return new RegisterRequest(
                "alice",
                "password123",
                "alice@gmail.com"
        );
    }

    private LoginRequest createLoginRequest() {
        return new LoginRequest(
                "alice",
                "password123"
        );
    }

    private LoginRequest createLoginRequestWithWrongPassword() {
        return new LoginRequest(
                "alice",
                "pass123word"
        );
    }

    @Test
    public void shouldHashPasswordWhenRegister() {
        RegisterRequest registerRequest = createRegisterRequest();

        userService.register(registerRequest);
        User savedUser = userRepository.findByUsername(registerRequest.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));

        assertNotEquals(savedUser.getPassword(), registerRequest.getPassword());
        assertTrue(passwordEncoder.matches(registerRequest.getPassword(), savedUser.getPassword()));
    }

    @Test
    public void shouldReturnTokenWithCorrectPasswordWhenLogin() {
        RegisterRequest registerRequest = createRegisterRequest();
        LoginRequest loginRequest = createLoginRequest();

        userService.register(registerRequest);
        AuthResponse authResponse = userService.login(loginRequest);

        assertNotNull(authResponse.getToken());
        assertFalse(authResponse.getToken().isBlank());
        assertNotNull(authResponse.getUsername());
    }

    @Test
    public void shouldReturnUnauthorizedWithIncorrectPasswordWhenLogin() {
        RegisterRequest registerRequest = createRegisterRequest();
        LoginRequest loginRequest = createLoginRequestWithWrongPassword();

        userService.register(registerRequest);

        assertThrows(BadCredentialsException.class,
                () -> userService.login(loginRequest));
    }

    @Test
    public void shouldReturnUnauthorizedWithoutToken() {
        ResponseEntity<String> response = testRestTemplate.getForEntity(
                "/api/habits",
                String.class
        );

        assertEquals(
                HttpStatus.UNAUTHORIZED,
                response.getStatusCode()
        );
    }

    @Test
    public void shouldSeeOnlyOwnHabits() {
        RegisterRequest aliceRegister = createRegisterRequest();
        testRestTemplate.postForEntity(
                "/api/auth/register",
                aliceRegister,
                String.class
        );
        ResponseEntity<AuthResponse> aliceLogin = testRestTemplate.postForEntity(
                "/api/auth/login",
                createLoginRequest(),
                AuthResponse.class
        );
        String aliceToken = aliceLogin.getBody().getToken();

        RegisterRequest bobRegister = new RegisterRequest(
                "bob",
                "password123",
                "bob@gmail.com"
        );
        testRestTemplate.postForEntity(
                "/api/auth/register",
                bobRegister,
                String.class
        );
        ResponseEntity<AuthResponse> bobLogin = testRestTemplate.postForEntity(
                "/api/auth/login",
                new LoginRequest("bob", "password123"),
                AuthResponse.class
        );
        String bobToken = bobLogin.getBody().getToken();

        HttpHeaders aliceHeaders = new HttpHeaders();
        aliceHeaders.setBearerAuth(aliceToken);
        HttpEntity<HabitRequest> aliceHabitRequest = new HttpEntity<>(
                new HabitRequest(
                      "Пить воду",
                        "10 раз в день",
                        10
                ),
                aliceHeaders
        );
        testRestTemplate.postForEntity(
                "/api/habits",
                aliceHabitRequest,
                HabitResponse.class
        );

        HttpHeaders bobHeaders = new HttpHeaders();
        bobHeaders.setBearerAuth(bobToken);
        HttpEntity<HabitRequest> bobHabitRequest = new HttpEntity<>(
                new HabitRequest(
                        "Ходить в зал",
                        "3 раза в неделю",
                        3
                ),
                bobHeaders
        );
        testRestTemplate.postForEntity(
                "/api/habits",
                bobHabitRequest,
                HabitResponse.class
        );

        ResponseEntity<HabitResponse[]> response = testRestTemplate.exchange(
                "/api/habits",
                HttpMethod.GET,
                new HttpEntity<>(aliceHeaders),
                HabitResponse[].class
        );

        HabitResponse[] habits = response.getBody();

        assertNotNull(habits);
        assertEquals(1, habits.length);
        assertEquals("Пить воду", habits[0].getName());
    }
}
