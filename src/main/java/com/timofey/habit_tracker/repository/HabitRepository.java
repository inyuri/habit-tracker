package com.timofey.habit_tracker.repository;

import com.timofey.habit_tracker.model.Habit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HabitRepository extends JpaRepository<Habit, Long> {
}
