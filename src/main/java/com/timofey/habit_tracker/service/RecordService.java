package com.timofey.habit_tracker.service;

import com.timofey.habit_tracker.dto.RecordResponse;
import com.timofey.habit_tracker.exception.HabitNotFoundException;
import com.timofey.habit_tracker.mapper.RecordMapper;
import com.timofey.habit_tracker.model.Habit;
import com.timofey.habit_tracker.model.Record;
import com.timofey.habit_tracker.repository.HabitRepository;
import com.timofey.habit_tracker.repository.RecordRepository;
import jakarta.transaction.Transactional;
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

    @Transactional
    public RecordResponse execute(Long habitId, boolean completed) {
        log.info("Creating new record by habit with habitId={}", habitId);

        Habit habit = habitRepository.findById(habitId).orElseThrow(() -> new HabitNotFoundException(""));
        Record record = new Record(habit, LocalDate.now(), completed);
        Record savedRecord = recordRepository.save(record);

        log.info("Record created successfully with id={}", savedRecord.getId());

        return recordMapper.toResponse(savedRecord);
    }

    public List<RecordResponse> getAll(Long habitId) {
        log.info("Getting all records by habitId={}", habitId);

        return recordRepository.findByHabitId(habitId).stream().map(recordMapper::toResponse).toList();
    }
}
