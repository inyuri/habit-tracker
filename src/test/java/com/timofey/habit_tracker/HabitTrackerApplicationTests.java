package com.timofey.habit_tracker;

import com.timofey.habit_tracker.dto.HabitRequest;
import com.timofey.habit_tracker.dto.HabitResponse;
import com.timofey.habit_tracker.exception.HabitNotFoundException;
import com.timofey.habit_tracker.service.HabitService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HabitTrackerApplicationTests {
    @Autowired
    private HabitService habitService;

    @Test
    public void shouldCreateHabit() {
        HabitRequest habitRequest = new HabitRequest(
                "Пить воду",
                "Пить 2л воды каждый день",
                10
        );

        HabitResponse createdHabitResponse = habitService.create(habitRequest);

        assertNotNull(createdHabitResponse);
        assertNotNull(createdHabitResponse.getId());
        assertNotNull(createdHabitResponse.getCreatedAt());

        assertEquals("Пить воду", createdHabitResponse.getName());
        assertEquals("Пить 2л воды каждый день", createdHabitResponse.getDescription());
        assertEquals(10, createdHabitResponse.getTarget());
    }

    @Test
    public void shouldFoundHabitById() {
        HabitRequest habitRequest = new HabitRequest(
                "Пить воду",
                "Пить 2л воды каждый день",
                10
        );

        HabitResponse createdHabitResponse = habitService.create(habitRequest);
        HabitResponse savedHabitResponse = habitService.getById(createdHabitResponse.getId());

        assertNotNull(createdHabitResponse);
        assertNotNull(savedHabitResponse);

        assertEquals(createdHabitResponse.getId(), savedHabitResponse.getId());
        assertEquals(createdHabitResponse.getName(), savedHabitResponse.getName());
        assertEquals(createdHabitResponse.getDescription(), savedHabitResponse.getDescription());
        assertEquals(createdHabitResponse.getTarget(), savedHabitResponse.getTarget());
    }

    @Test
    void shouldThrowHabitNotFoundException() {
        Long nonExistingId = 999999L;

        assertThrows(HabitNotFoundException.class, () -> {
            habitService.getById(nonExistingId);
        });
    }
}
