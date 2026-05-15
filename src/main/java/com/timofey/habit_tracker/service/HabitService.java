package com.timofey.habit_tracker.service;

import com.timofey.habit_tracker.dto.HabitRequest;
import com.timofey.habit_tracker.dto.HabitResponse;
import com.timofey.habit_tracker.exception.HabitNotFoundException;
import com.timofey.habit_tracker.model.Habit;
import com.timofey.habit_tracker.repository.HabitRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class HabitService {

    private final HabitRepository habitRepository;

    public HabitService(HabitRepository habitRepository) {
        this.habitRepository = habitRepository;
    }

    public HabitResponse create(HabitRequest habitRequest) {
        log.info("Creating habit with name={}, description={}, target={}",
                habitRequest.getName(),
                habitRequest.getDescription(),
                habitRequest.getTarget());

        Habit habit = new Habit(habitRequest.getName(),
                                habitRequest.getDescription(),
                                habitRequest.getTarget());

        Habit savedHabit = habitRepository.save(habit);

        log.info("Habit created successfully with id={}", savedHabit.getId());

        return toResponse(savedHabit);
    }

    public List<HabitResponse> getAll() {
        log.info("Receive all habits");

        return habitRepository.findAll()
                              .stream()
                              .map(this::toResponse)
                              .toList();
    }

    public HabitResponse getById(Long id) {
        log.info("Searching habit by id={}", id);

        Habit habit = habitRepository.findById(id).orElseThrow(() -> new HabitNotFoundException(""));
        return toResponse(habit);
    }

    public HabitResponse update(Long id, HabitRequest habitRequest) {
        log.info("Updating habit by id={}", id);

        Habit habit = habitRepository.findById(id).orElseThrow(() -> new HabitNotFoundException(""));

        habit.setName(habitRequest.getName());
        habit.setDescription(habitRequest.getDescription());
        habit.setTarget(habitRequest.getTarget());

        Habit updatedHabit = habitRepository.save(habit);

        log.info("Habit updated successfully by habit with name={}, description={}, target={}",
                habitRequest.getName(), habitRequest.getDescription(), habitRequest.getTarget());

        return toResponse(updatedHabit);
    }

    public void delete(Long id) {
        log.info("Deleting habit by id={}", id);

        Habit habit = habitRepository.findById(id).orElseThrow(() -> new HabitNotFoundException(""));
        habitRepository.delete(habit);

        log.info("Habit by id={} successfully deleted", id);
    }

    private HabitResponse toResponse(Habit habit) {
        return new HabitResponse(habit.getId(),
                                 habit.getName(),
                                 habit.getDescription(),
                                 habit.getTarget(),
                                 habit.getCreatedAt());
    }
}
