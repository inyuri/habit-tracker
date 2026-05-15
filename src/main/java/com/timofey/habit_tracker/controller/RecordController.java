package com.timofey.habit_tracker.controller;

import com.timofey.habit_tracker.dto.RecordResponse;
import com.timofey.habit_tracker.service.RecordService;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/habits")
public class RecordController {

    private final RecordService recordService;

    public RecordController(RecordService recordService) {
        this.recordService = recordService;
    }

    @PostMapping("/{id}/records")
    public ResponseEntity<RecordResponse> execute(@Positive @PathVariable Long id) {
        log.info("POST /api/habits/{}/records request received", id);

        RecordResponse recordResponse = recordService.execute(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(recordResponse);
    }

    @GetMapping("/{id}/records")
    public ResponseEntity<List<RecordResponse>> getAll(@Positive @PathVariable Long id) {
        log.info("GET /api/habits/{}/records request received", id);

        return ResponseEntity.ok(recordService.getAll(id));
    }
}
