package com.timofey.habit_tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.timofey.habit_tracker.model.Record;

import java.time.LocalDate;
import java.util.List;

public interface RecordRepository extends JpaRepository<Record, Long> {
    List<Record> findByHabitId(Long habitId);
    boolean existsByHabitIdAndDate(Long habitId, LocalDate date);
}
