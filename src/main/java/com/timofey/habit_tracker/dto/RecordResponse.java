package com.timofey.habit_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class RecordResponse {
    private Long habitId;
    private LocalDate date;
}
