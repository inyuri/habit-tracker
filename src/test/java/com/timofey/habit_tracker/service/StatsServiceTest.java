package com.timofey.habit_tracker.service;

import com.timofey.habit_tracker.model.Habit;
import com.timofey.habit_tracker.model.Record;
import com.timofey.habit_tracker.model.User;
import com.timofey.habit_tracker.repository.HabitRepository;
import com.timofey.habit_tracker.repository.RecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StatsServiceTest {

    @Mock
    private RecordRepository recordRepository;

    @Mock
    private HabitRepository habitRepository;

    @InjectMocks
    private StatsService statsService;

    private Habit habit;

    @BeforeEach
    void setUp() {
        User user = new User(
                "Anton",
                "123232",
                "anton@gmail.com"
        );

        habit = new Habit(
                "Пить воду",
                "Каждый день 2 литра",
                10,
                user
        );

        habit.setId(1L);
        habit.setCreatedAt(LocalDateTime.now().minusDays(4));
    }

    @Test
    void shouldCalculateCurrentStreak() {
        LocalDate today = LocalDate.now();

        when(recordRepository.findByHabitId(1L))
                .thenReturn(List.of(
                        new Record(
                                habit,
                                today,
                                true
                        ),

                        new Record(
                                habit,
                                today.minusDays(1),
                                true
                        ),

                        new Record(
                                habit,
                                today.minusDays(2),
                                true
                        )
                ));

        int streak = statsService.getCurrentStreak(1L);

        assertEquals(3, streak);

        verify(recordRepository).findByHabitId(1L);
    }

    @Test
    void shouldBreakCurrentStreakWhenDayIsMissed() {
        LocalDate today = LocalDate.now();

        when(recordRepository.findByHabitId(1L))
                .thenReturn(List.of(
                        new Record(
                                habit,
                                today,
                                true
                        ),

                        new Record(
                                habit,
                                today.minusDays(1),
                                true
                        ),

                        new Record(
                                habit,
                                today.minusDays(3),
                                true
                        )
                ));

        int streak = statsService.getCurrentStreak(1L);

        assertEquals(2, streak);

        verify(recordRepository).findByHabitId(1L);
    }

    @Test
    void shouldCalculateBestStreak() {
        LocalDate today = LocalDate.now();

        when(recordRepository.findByHabitId(1L))
                .thenReturn(List.of(
                        new Record(
                                habit,
                                today.minusDays(5),
                                true
                        ),

                        new Record(
                                habit,
                                today.minusDays(4),
                                true
                        ),

                        new Record(
                                habit,
                                today.minusDays(3),
                                true
                        ),

                        new Record(
                                habit,
                                today.minusDays(1),
                                true
                        )
                ));

        int bestStreak = statsService.getBestStreak(habit.getId());

        assertEquals(3, bestStreak);

        verify(recordRepository).findByHabitId(1L);
    }

    @Test
    void shouldCalculateCompletionRate() {
        LocalDate today = LocalDate.now();

        when(habitRepository.findById(1L)).thenReturn(Optional.of(habit));
        when(recordRepository.findByHabitId(1L))
                .thenReturn(List.of(

                        new Record(
                                habit,
                                today,
                                true
                        ),

                        new Record(
                                habit,
                                today.minusDays(1),
                                true
                        ),

                        new Record(
                                habit,
                                today.minusDays(2),
                                false
                        ),

                        new Record(
                                habit,
                                today.minusDays(3),
                                false
                        )
                ));

        double rate = statsService.getCompletionRate(1L);

        assertEquals(50.0, rate, 0.01);

        verify(habitRepository).findById(1L);
        verify(recordRepository).findByHabitId(1L);
    }
}
