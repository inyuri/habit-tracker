package com.timofey.habit_tracker.mapper;

import com.timofey.habit_tracker.dto.RecordResponse;
import com.timofey.habit_tracker.model.Record;
import org.springframework.stereotype.Component;

@Component
public class RecordMapper {
    public RecordResponse toResponse(Record record) {
        return new RecordResponse(
                record.getHabit().getId(),
                record.getDate(),
                record.isCompleted()
        );
    }
}
