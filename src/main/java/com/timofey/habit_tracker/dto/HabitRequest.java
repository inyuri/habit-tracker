package com.timofey.habit_tracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class HabitRequest {
    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @Positive(message = "Target must be a positive number")
    private int target;

    public HabitRequest(String name, String description, int target) {
        this.name = name;
        this.description = description;
        this.target = target;
    }
}
