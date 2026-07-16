package com.timofey.habit_tracker.controller;

import com.timofey.habit_tracker.dto.DailyStatsResponse;
import com.timofey.habit_tracker.dto.WeekStatsResponse;
import com.timofey.habit_tracker.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name = "Statistics",
        description = "Статистика выполнения привычек"
)
public class StatsController {

    private final StatsService statsService;

    @GetMapping("/daily")
    @Operation(
            summary = "Получить статистику за день",
            description = "Возвращает статистику выполнения привычек пользователя за текущий день"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Дневная статистика успешно получена"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не авторизован"
            )
    })
    public ResponseEntity<DailyStatsResponse> getDailyStats() {
        log.info("GET /api/stats/daily");

        DailyStatsResponse dailyStatsResponse = statsService.getDailyStatsResponse();

        return ResponseEntity.ok(dailyStatsResponse);
    }

    @GetMapping("/week")
    @Operation(
            summary = "Получить статистику за неделю",
            description = "Возвращает статистику выполнения привычек пользователя за последние 7 дней"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Недельная статистика успешно получена"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не авторизован"
            )
    })
    public ResponseEntity<WeekStatsResponse> getWeekStats() {
        log.info("GET /api/stats/week");

        WeekStatsResponse weekStatsResponse = statsService.getWeekStatsResponse();

        return ResponseEntity.ok(weekStatsResponse);
    }

}
