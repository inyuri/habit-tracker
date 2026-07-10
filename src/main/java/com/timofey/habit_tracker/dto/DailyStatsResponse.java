package com.timofey.habit_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class DailyStatsResponse {
    private LocalDate date;
    private List<RecordResponse> dailyRecords;
}
