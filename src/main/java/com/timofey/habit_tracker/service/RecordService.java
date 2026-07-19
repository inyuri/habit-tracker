package com.timofey.habit_tracker.service;

import com.timofey.habit_tracker.dto.RecordResponse;
import com.timofey.habit_tracker.exception.HabitNotFoundException;
import com.timofey.habit_tracker.exception.RecordAlreadyExistsException;
import com.timofey.habit_tracker.mapper.RecordMapper;
import com.timofey.habit_tracker.model.Habit;
import com.timofey.habit_tracker.model.Record;
import com.timofey.habit_tracker.model.User;
import com.timofey.habit_tracker.repository.HabitRepository;
import com.timofey.habit_tracker.repository.RecordRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecordService {

    private final RecordRepository recordRepository;
    private final HabitRepository habitRepository;
    private final RecordMapper recordMapper;
    private final CurrentUserService currentUserService;

    @Transactional
    public RecordResponse execute(Long habitId, boolean completed) {
        log.info("Creating new record by habit with habitId={}", habitId);

        User user = currentUserService.getCurrentUser();

        LocalDate today = LocalDate.now();

        Habit habit = habitRepository.findByIdAndUserId(habitId, user.getId())
                .orElseThrow(() -> new HabitNotFoundException("Habit not found"));

        if (recordRepository.existsByHabitIdAndDate(habitId, today)) {
            throw new RecordAlreadyExistsException("Record already exists");
        }

        Record record = new Record(habit, today, completed);
        Record savedRecord = recordRepository.save(record);

        log.info("Record created successfully with id={} for user={}",
                savedRecord.getId(),
                user.getUsername());

        return recordMapper.toResponse(savedRecord);
    }

    @Transactional(readOnly = true)
    public List<RecordResponse> getAll(Long habitId) {
        log.info("Getting all records by habitId={}", habitId);

        User user = currentUserService.getCurrentUser();

        habitRepository.findByIdAndUserId(habitId, user.getId())
                .orElseThrow(() -> new HabitNotFoundException("Habit not found"));

        return recordRepository.findByHabitIdAndHabitUserId(habitId, user.getId())
                .stream()
                .map(recordMapper::toResponse)
                .toList();
    }
}
