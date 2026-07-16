package com.timofey.habit_tracker.mapper;

import com.timofey.habit_tracker.dto.HabitResponse;
import com.timofey.habit_tracker.model.Habit;
import org.springframework.stereotype.Component;

@Component
public class HabitMapper {
    public HabitResponse toResponse(Habit habit) {
        return new HabitResponse(
                habit.getId(),
                habit.getName(),
                habit.getDescription(),
                habit.getTarget()
        );
    }
}
