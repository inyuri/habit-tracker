package com.timofey.habit_tracker.service;

import com.timofey.habit_tracker.model.Habit;
import com.timofey.habit_tracker.model.Record;
import com.timofey.habit_tracker.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatsServiceTest {

    private StatsService statsService;

    private Habit habit;

    @BeforeEach
    void setUp() {
        statsService = new StatsService(
                null,
                null,
                null
        );
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

        List<Record> records = List.of(
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
        );

        int streak = statsService.getCurrentStreak(records);

        assertEquals(3, streak);
    }


    @Test
    void shouldBreakCurrentStreakWhenDayIsMissed() {
        LocalDate today = LocalDate.now();

        List<Record> records = List.of(
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
        );

        int streak = statsService.getCurrentStreak(records);

        assertEquals(2, streak);
    }


    @Test
    void shouldCalculateBestStreak() {
        LocalDate today = LocalDate.now();

        List<Record> records = List.of(
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
        );

        int bestStreak = statsService.getBestStreak(records);

        assertEquals(3, bestStreak);
    }


    @Test
    void shouldCalculateCompletionRate() {
        LocalDate today = LocalDate.now();

        List<Record> records = List.of(
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
        );

        double rate = statsService.getCompletionRate(
                records,
                habit
        );

        assertEquals(50.0, rate, 0.01);
    }
}