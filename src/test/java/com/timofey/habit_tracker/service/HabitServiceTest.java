package com.timofey.habit_tracker.service;

import com.timofey.habit_tracker.dto.HabitRequest;
import com.timofey.habit_tracker.dto.HabitResponse;
import com.timofey.habit_tracker.exception.HabitNotFoundException;
import com.timofey.habit_tracker.mapper.HabitMapper;
import com.timofey.habit_tracker.model.Habit;
import com.timofey.habit_tracker.model.User;
import com.timofey.habit_tracker.repository.HabitRepository;
import com.timofey.habit_tracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HabitServiceTest {

    @Mock
    private HabitRepository habitRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HabitMapper habitMapper;

    @InjectMocks
    private HabitService habitService;

    private User user;
    private Habit habit;
    private HabitRequest habitRequest;
    private HabitResponse habitResponse;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("alice");

        habit = new Habit(
                "Пить воду",
                "Каждый день 2 литра",
                10,
                user
        );

        habitRequest = new HabitRequest(
                "Пить воду",
                "Каждый день 2 литра",
                10
        );

        habitResponse = new HabitResponse(
                1L,
                "Пить воду",
                "Каждый день 2 литра",
                10
        );

        SecurityContextHolder.getContext()
                .setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                "alice",
                                null
                        )
                );

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
    }

    @Test
    void shouldCreateHabit() {
        when(habitRepository.save(any(Habit.class))).thenReturn(habit);
        when(habitMapper.toResponse(any(Habit.class))).thenReturn(habitResponse);

        HabitResponse habitResponse = habitService.create(habitRequest);

        assertNotNull(habitResponse);
        assertEquals("Пить воду", habitResponse.getName());

        verify(habitRepository).save(any(Habit.class));
        verify(habitMapper).toResponse(habit);
    }

    @Test
    void shouldGetAllHabits() {
        when(habitRepository.findAllByUserId(1L)).thenReturn(List.of(habit));
        when(habitMapper.toResponse(any(Habit.class))).thenReturn(habitResponse);

        List<HabitResponse> habitResponseList = habitService.getAll();

        assertNotNull(habitResponseList);
        assertEquals(1, habitResponseList.size());
        assertEquals("Пить воду", habitResponseList.getFirst().getName());

        verify(habitRepository).findAllByUserId(1L);
        verify(habitMapper).toResponse(habit);
    }

    @Test
    void shouldGetHabitById() {
        when(habitRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(habit));
        when(habitMapper.toResponse(any(Habit.class))).thenReturn(habitResponse);

        HabitResponse habitResponse = habitService.getById(1L);

        assertNotNull(habitResponse);
        assertEquals("Пить воду", habitResponse.getName());

        verify(habitRepository).findByIdAndUserId(1L, 1L);
        verify(habitMapper).toResponse(habit);
    }

    @Test
    void shouldUpdateHabit() {
        when(habitRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(habit));
        when(habitRepository.save(any(Habit.class))).thenReturn(habit);
        when(habitMapper.toResponse(any(Habit.class))).thenReturn(habitResponse);

        HabitResponse habitResponse = habitService.update(1L, habitRequest);

        assertNotNull(habitResponse);
        assertEquals("Пить воду", habitResponse.getName());

        verify(habitRepository).findByIdAndUserId(1L, 1L);
        verify(habitRepository).save(any(Habit.class));
        verify(habitMapper).toResponse(habit);
    }

    @Test
    void shouldDeleteHabit() {
        when(habitRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(habit));

        habitService.delete(1L);

        verify(habitRepository).delete(habit);
    }

    @Test
    void shouldThrowExceptionWhenHabitNotFound() {
        when(habitRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

        assertThrows(HabitNotFoundException.class,
                () -> habitService.getById(99L));

        verify(habitRepository).findByIdAndUserId(99L, 1L);
    }
}
