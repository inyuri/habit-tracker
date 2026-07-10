package com.timofey.habit_tracker.controller;

import com.timofey.habit_tracker.dto.HabitRequest;
import com.timofey.habit_tracker.dto.HabitResponse;
import com.timofey.habit_tracker.dto.StatsResponse;
import com.timofey.habit_tracker.service.HabitService;
import com.timofey.habit_tracker.service.StatsService;
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
public class HabitController {

    private final HabitService habitService;
    private final StatsService statsService;

    @PostMapping
    public ResponseEntity<HabitResponse> create(@Valid @RequestBody HabitRequest request) {
        log.info("POST /api/habits");

        HabitResponse created = habitService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<HabitResponse>> getAll() {
        log.info("GET /api/habits");

        return ResponseEntity.ok(habitService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HabitResponse> getById(@Positive @PathVariable Long id) {
        log.info("GET /api/habits/{}", id);

        HabitResponse habit = habitService.getById(id);
        return ResponseEntity.ok(habit);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HabitResponse> update(@Positive @PathVariable Long id,
                                                @Valid @RequestBody HabitRequest habitRequest) {
        log.info("PUT /api/habits/{}", id);

        HabitResponse habit = habitService.update(id, habitRequest);
        return ResponseEntity.ok(habit);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HabitResponse> delete(@Positive @PathVariable Long id) {
        log.info("DELETE /api/habits/{}", id);

        habitService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<StatsResponse> getStats(@Positive @PathVariable Long id) {
        log.info("GET /api/habits/{}/stats", id);

        StatsResponse statsResponse = statsService.getStatsResponse(id);

        return ResponseEntity.ok(statsResponse);
    }
}
