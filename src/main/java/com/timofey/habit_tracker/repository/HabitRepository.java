package com.timofey.habit_tracker.repository;

import com.timofey.habit_tracker.model.Habit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {
    List<Habit> findAllByUserId(Long id);
    Optional<Habit> findByIdAndUserId(Long id, Long userId);
}
