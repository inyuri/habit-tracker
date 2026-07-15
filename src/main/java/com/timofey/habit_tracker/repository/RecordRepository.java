package com.timofey.habit_tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.timofey.habit_tracker.model.Record;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {
    List<Record> findByHabitId(Long habitId);
}
