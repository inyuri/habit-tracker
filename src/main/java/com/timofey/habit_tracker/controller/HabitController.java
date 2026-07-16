package com.timofey.habit_tracker.controller;

import com.timofey.habit_tracker.dto.HabitRequest;
import com.timofey.habit_tracker.dto.HabitResponse;
import com.timofey.habit_tracker.dto.StatsResponse;
import com.timofey.habit_tracker.service.HabitService;
import com.timofey.habit_tracker.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/habits")
@RequiredArgsConstructor
@Tag(
        name = "Habits",
        description = "Управление привычками пользователя"
)
public class HabitController {

    private final HabitService habitService;
    private final StatsService statsService;

    @PostMapping
    @Operation(
            summary = "Создать привычку",
            description = "Создает новую привычку для текущего пользователя"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Привычка успешно создана"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
    })
    public ResponseEntity<HabitResponse> create(@Valid @RequestBody HabitRequest request) {
        log.info("POST /api/habits");

        HabitResponse created = habitService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Operation(
            summary = "Получить все привычки",
            description = "Возвращает список привычек текущего пользователя"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список привычек получен"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
    })
    public ResponseEntity<List<HabitResponse>> getAll() {
        log.info("GET /api/habits");

        return ResponseEntity.ok(habitService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получить привычку по ID",
            description = "Возвращает конкретную привычку текущего пользователя"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Привычка найдена"),
            @ApiResponse(responseCode = "404", description = "Привычка не найдена"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
    })
    public ResponseEntity<HabitResponse> getById(@Positive @PathVariable Long id) {
        log.info("GET /api/habits/{}", id);

        HabitResponse habit = habitService.getById(id);
        return ResponseEntity.ok(habit);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Обновить привычку",
            description = "Изменяет данные существующей привычки"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Привычка обновлена"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "404", description = "Привычка не найдена")
    })
    public ResponseEntity<HabitResponse> update(@Positive @PathVariable Long id,
                                                @Valid @RequestBody HabitRequest habitRequest) {
        log.info("PUT /api/habits/{}", id);

        HabitResponse habit = habitService.update(id, habitRequest);
        return ResponseEntity.ok(habit);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Удалить привычку",
            description = "Удаляет привычку текущего пользователя"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Привычка удалена"),
            @ApiResponse(responseCode = "404", description = "Привычка не найдена")
    })
    public ResponseEntity<HabitResponse> delete(@Positive @PathVariable Long id) {
        log.info("DELETE /api/habits/{}", id);

        habitService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/stats")
    @Operation(
            summary = "Получить статистику привычки",
            description = "Возвращает серию выполнений, лучший результат и процент выполнения"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Статистика получена"),
            @ApiResponse(responseCode = "404", description = "Привычка не найдена")
    })
    public ResponseEntity<StatsResponse> getStats(@Positive @PathVariable Long id) {
        log.info("GET /api/habits/{}/stats", id);

        StatsResponse statsResponse = statsService.getStatsResponse(id);

        return ResponseEntity.ok(statsResponse);
    }
}
