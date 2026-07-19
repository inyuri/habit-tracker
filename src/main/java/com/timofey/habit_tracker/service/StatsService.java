package com.timofey.habit_tracker.service;

import com.timofey.habit_tracker.dto.*;
import com.timofey.habit_tracker.exception.HabitNotFoundException;
import com.timofey.habit_tracker.model.Habit;
import com.timofey.habit_tracker.model.User;
import com.timofey.habit_tracker.repository.HabitRepository;
import com.timofey.habit_tracker.repository.RecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.timofey.habit_tracker.model.Record;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsService {

    private final RecordRepository recordRepository;
    private final HabitRepository habitRepository;
    private final CurrentUserService currentUserService;

    public int getCurrentStreak(List<Record> records) {
        int count = 0;
        LocalDate current = LocalDate.now();

        List<Record> completedRecords = records
                .stream()
                .sorted(Comparator.comparing(Record::getDate).reversed())
                .filter(Record::isCompleted)
                .toList();

        for (Record record : completedRecords) {
            if (record.getDate().equals(current)) {
                count++;
                current = current.minusDays(1);
            }
            else {
                break;
            }
        }

        return count;
    }

    public int getBestStreak(List<Record> records) {
        List<Record> completedRecords = records
                .stream()
                .sorted(Comparator.comparing(Record::getDate))
                .filter(Record::isCompleted)
                .toList();

        int currentStreak = 0;
        int bestStreak = 0;
        LocalDate prevDate = null;

        for (Record record : completedRecords) {
            LocalDate date = record.getDate();

            if (prevDate == null) {
                currentStreak = 1;
            }
            else if (date.equals(prevDate.plusDays(1))) {
                currentStreak++;
            }
            else {
                currentStreak = 1;
            }

            bestStreak = Math.max(currentStreak, bestStreak);
            prevDate = date;
        }

        return bestStreak;
    }

    public double getCompletionRate(List<Record> records, Habit habit) {
        long totalDays = ChronoUnit.DAYS.between(
                habit.getCreatedAt().toLocalDate(),
                LocalDate.now()
        );

        long completedDays = records.stream()
                .filter(Record::isCompleted)
                .map(Record::getDate)
                .distinct()
                .count();

        if (totalDays <= 0) {
            return 0.0;
        }

        return ((double) completedDays / totalDays) * 100.0;
    }

    @Transactional(readOnly = true)
    public StatsResponse getStatsResponse(Long habitId) {
        log.info("Searching stats for habit with id={}", habitId);

        User user = currentUserService.getCurrentUser();

        Habit habit = habitRepository
                .findByIdAndUserId(habitId, user.getId())
                .orElseThrow(() -> new HabitNotFoundException("Habit not found"));

        List<Record> records = recordRepository
                .findByHabitIdAndHabitUserId(habitId, user.getId());

        return new StatsResponse(
                getCurrentStreak(records),
                getBestStreak(records),
                getCompletionRate(records, habit),
                (int) ChronoUnit.DAYS.between(
                        habit.getCreatedAt().toLocalDate(),
                        LocalDate.now()
                )
        );
    }

    @Transactional(readOnly = true)
    public DailyStatsResponse getDailyStatsResponse() {
        User user = currentUserService.getCurrentUser();

        LocalDate today = LocalDate.now();

        List<RecordResponse> dailyRecords = recordRepository
                .findDailyRecordsByUserId(user.getId(), today)
                .stream()
                .map(record -> new RecordResponse(
                        record.getHabit().getId(),
                        record.getDate(),
                        record.isCompleted()
                ))
                .toList();

        return new DailyStatsResponse(
                today,
                dailyRecords
        );
    }

    @Transactional(readOnly = true)
    public WeekStatsResponse getWeekStatsResponse() {
        User user = currentUserService.getCurrentUser();

        List<Record> allRecords = recordRepository.findByHabitUserId(user.getId());

        List<DayStatsResponse> dayStatsResponses = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);

            List<Record> dayRecords = allRecords
                    .stream()
                    .filter(record -> record.getDate().equals(date))
                    .toList();

            int completedCount = (int) dayRecords
                    .stream()
                    .filter(Record::isCompleted)
                    .count();

            int totalCount = dayRecords.size();

            DayStatsResponse dayStatsResponse = new DayStatsResponse(date, completedCount, totalCount);

            dayStatsResponses.add(dayStatsResponse);
        }

        return new WeekStatsResponse(dayStatsResponses);
    }
}
