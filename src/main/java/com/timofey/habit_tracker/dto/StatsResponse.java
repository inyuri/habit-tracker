package com.timofey.habit_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatsResponse {
    private int currentStreak;
    private int bestStreak;
    private double completionRate;
    private int totalCompletions;
}
