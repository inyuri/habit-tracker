package com.timofey.habit_tracker;

import com.timofey.habit_tracker.model.Habit;
import com.timofey.habit_tracker.model.Record;
import com.timofey.habit_tracker.repository.HabitRepository;
import com.timofey.habit_tracker.repository.RecordRepository;
import com.timofey.habit_tracker.service.StatsService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class StatsServiceTest {
    @Autowired
    private StatsService statsService;

    @Autowired
    private RecordRepository recordRepository;

    @Autowired
    private HabitRepository habitRepository;

    private Habit saveHabit() {
        Habit habit = new Habit(
                "Пить воду",
                "Пить 2л воды каждый день",
                10
        );

        return habitRepository.save(habit);
    }

    private void record(Habit habit, LocalDate date, boolean isCompleted) {
        recordRepository.save(new Record(habit, date, isCompleted));
    }

    @Test
    void shouldCalculateCurrentStreak() {
        Habit habit = saveHabit();

        LocalDate today = LocalDate.now();

        record(habit, today, true);
        record(habit, today.minusDays(1), true);
        record(habit, today.minusDays(2), true);

        int streak = statsService.getCurrentStreak(habit.getId());

        assertEquals(3, streak);
    }

    @Test
    void shouldBreakCurrentStreakWhenDayIsMissed() {
        Habit habit = saveHabit();

        LocalDate today = LocalDate.now();

        record(habit, today, true);
        record(habit, today.minusDays(1), true);

        record(habit, today.minusDays(3), true);

        int streak = statsService.getCurrentStreak(habit.getId());

        assertEquals(2, streak);
    }

    @Test
    void shouldCalculateBestStreak() {
        Habit habit = saveHabit();

        LocalDate today = LocalDate.now();

        record(habit, today, true);
        record(habit, today.minusDays(1), true);
        record(habit, today.minusDays(2), true);

        record(habit, today.minusDays(3), false);
        record(habit, today.minusDays(4), false);

        record(habit, today.minusDays(5), true);
        record(habit, today.minusDays(6), true);

        int bestStreak = statsService.getBestStreak(habit.getId());

        assertEquals(3, bestStreak);
    }

    @Test
    void shouldCalculateCompletionRate() {
        Habit habit = saveHabit();

        LocalDate today = LocalDate.now();

        habit.setCreatedAt(today
                .minusDays(3)
                .atStartOfDay());

        habitRepository.save(habit);

        record(habit, today, true);
        record(habit, today.minusDays(1), true);

        record(habit, today.minusDays(2), false);
        record(habit, today.minusDays(3), false);

        double completionRate = statsService.getCompletionRate(habit.getId());

        assertEquals(50.0, completionRate, 0.01);
    }
}
