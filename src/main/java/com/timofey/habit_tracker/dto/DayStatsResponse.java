package com.timofey.habit_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class DayStatsResponse {
    private LocalDate date;
    private int completedCount;
    private int totalCount;
}
