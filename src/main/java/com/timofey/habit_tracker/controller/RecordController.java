package com.timofey.habit_tracker.controller;

import com.timofey.habit_tracker.dto.RecordResponse;
import com.timofey.habit_tracker.service.RecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/habits")
@Tag(
        name = "Records",
        description = "Управление записями выполнения привычек"
)
@Validated
public class RecordController {

    private final RecordService recordService;

    public RecordController(RecordService recordService) {
        this.recordService = recordService;
    }

    @PostMapping("/{id}/records")
    @Operation(
            summary = "Добавить запись выполнения привычки",
            description = "Создает запись о выполнении или невыполнении привычки"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Запись успешно создана"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректный ID привычки или параметры запроса"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не авторизован"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Привычка не найдена"
            )
    })
    public ResponseEntity<RecordResponse> execute(@Positive @PathVariable Long id,
                                                  @RequestParam boolean completed) {
        log.info("POST /api/habits/{}/records", id);

        RecordResponse recordResponse = recordService.execute(id, completed);
        return ResponseEntity.status(HttpStatus.CREATED).body(recordResponse);
    }

    @GetMapping("/{id}/records")
    @Operation(
            summary = "Получить историю выполнения привычки",
            description = "Возвращает список всех записей выполнения указанной привычки"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "История успешно получена"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректный ID привычки"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не авторизован"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Привычка не найдена"
            )
    })
    public ResponseEntity<List<RecordResponse>> getAll(@Positive @PathVariable Long id) {
        log.info("GET /api/habits/{}/records", id);

        return ResponseEntity.ok(recordService.getAll(id));
    }
}
