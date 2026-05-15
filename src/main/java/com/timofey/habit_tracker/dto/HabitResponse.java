package com.timofey.habit_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class HabitResponse {
    private Long id;
    private String name;
    private String description;
    private int target;
    private LocalDateTime createdAt;
}
