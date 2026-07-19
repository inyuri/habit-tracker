package com.timofey.habit_tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.timofey.habit_tracker.model.Record;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {
    List<Record> findByHabitIdAndHabitUserId(Long habitId, Long userId);
    List<Record> findByHabitUserId(Long userId);
    boolean existsByHabitIdAndDate(Long habitId, LocalDate date);

    @Query("SELECT r FROM Record r JOIN FETCH r.habit WHERE r.habit.id = :userId AND r.date = :date")
    List<Record> findDailyRecordsByUserId(@Param("userId") Long userId, @Param("date") LocalDate date);
}
