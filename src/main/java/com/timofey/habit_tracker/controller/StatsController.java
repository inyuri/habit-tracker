package com.timofey.habit_tracker.controller;

import com.timofey.habit_tracker.dto.DailyStatsResponse;
import com.timofey.habit_tracker.dto.WeekStatsResponse;
import com.timofey.habit_tracker.service.StatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @GetMapping("/daily")
    public ResponseEntity<DailyStatsResponse> getDailyStats() {
        log.info("GET /api/stats/daily");

        DailyStatsResponse dailyStatsResponse = statsService.getDailyStatsResponse();

        return ResponseEntity.ok(dailyStatsResponse);
    }

    @GetMapping("/week")
    public ResponseEntity<WeekStatsResponse> getWeekStats() {
        log.info("GET /api/stats/week");

        WeekStatsResponse weekStatsResponse = statsService.getWeekStatsResponse();

        return ResponseEntity.ok(weekStatsResponse);
    }

}
