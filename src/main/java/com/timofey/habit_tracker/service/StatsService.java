package com.timofey.habit_tracker.service;

import com.timofey.habit_tracker.dto.*;
import com.timofey.habit_tracker.exception.HabitNotFoundException;
import com.timofey.habit_tracker.model.Habit;
import com.timofey.habit_tracker.repository.HabitRepository;
import com.timofey.habit_tracker.repository.RecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.timofey.habit_tracker.model.Record;

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

    public int getCurrentStreak(Long habitId) {
        int count = 0;
        LocalDate current = LocalDate.now();

        List<Record> records = recordRepository.findByHabitId(habitId)
                .stream()
                .sorted(Comparator.comparing(Record::getDate).reversed())
                .filter(Record::isCompleted)
                .toList();

        for (Record record : records) {
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

    public int getBestStreak(Long habitId) {
        List<Record> records = recordRepository.findByHabitId(habitId)
                .stream()
                .sorted(Comparator.comparing(Record::getDate))
                .filter(Record::isCompleted)
                .toList();

        int currentStreak = 0;
        int bestStreak = 0;
        LocalDate prevDate = null;

        for (Record record : records) {
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

    public double getCompletionRate(Long habitId) {
        long totalDays = getTotalCompletions(habitId);

        List<Record> records = recordRepository.findByHabitId(habitId);
        long completedDays = records.stream()
                .filter(Record::isCompleted)
                .map(Record::getDate)
                .distinct()
                .count();

        return ((double) completedDays / totalDays) * 100.0;
    }

    private long getTotalCompletions(Long habitId) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new HabitNotFoundException("Habit not found"));

        LocalDate createdAt = habit.getCreatedAt().toLocalDate();

        return ChronoUnit.DAYS.between(createdAt, LocalDate.now());
    }

    public StatsResponse getStatsResponse(Long habitId) {
        log.info("Searching stats for habit with id={}", habitId);

        StatsResponse statsResponse;

        statsResponse = new StatsResponse(
                getCurrentStreak(habitId),
                getBestStreak(habitId),
                getCompletionRate(habitId),
                (int) getTotalCompletions(habitId)
        );

        return statsResponse;
    }

    public DailyStatsResponse getDailyStatsResponse() {
        List<RecordResponse> dailyRecords = recordRepository.findAll()
                .stream()
                .filter(record -> record.getDate().equals(LocalDate.now()))
                .map(record -> new RecordResponse(
                        record.getHabit().getId(),
                        record.getDate(),
                        record.isCompleted()
                ))
                .toList();

        DailyStatsResponse dailyStatsResponse;

        dailyStatsResponse = new DailyStatsResponse(
                LocalDate.now(),
                dailyRecords
        );

        return dailyStatsResponse;
    }

    public WeekStatsResponse getWeekStatsResponse() {
        List<Record> allRecords = recordRepository.findAll();

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
