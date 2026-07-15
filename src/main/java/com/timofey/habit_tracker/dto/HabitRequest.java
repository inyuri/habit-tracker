package com.timofey.habit_tracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HabitRequest {
    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @Positive(message = "Target must be a positive number")
    private int target;
}
