package com.timofey.habit_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class WeekStatsResponse {
    List<DayStatsResponse> week;
}
