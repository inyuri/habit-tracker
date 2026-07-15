package com.timofey.habit_tracker.service;

import com.timofey.habit_tracker.dto.HabitRequest;
import com.timofey.habit_tracker.dto.HabitResponse;
import com.timofey.habit_tracker.exception.HabitNotFoundException;
import com.timofey.habit_tracker.model.Habit;
import com.timofey.habit_tracker.model.User;
import com.timofey.habit_tracker.repository.HabitRepository;
import com.timofey.habit_tracker.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HabitService {

    private final HabitRepository habitRepository;
    private final UserRepository userRepository;

    @Transactional
    public HabitResponse create(HabitRequest habitRequest) {
        User user = getCurrentUser();

        log.info("Creating habit with name={}, description={}, target={} for user={}",
                habitRequest.getName(),
                habitRequest.getDescription(),
                habitRequest.getTarget(),
                user.getUsername());

        Habit habit = new Habit(habitRequest.getName(),
                                habitRequest.getDescription(),
                                habitRequest.getTarget(),
                                user);

        Habit savedHabit = habitRepository.save(habit);

        log.info("Habit created successfully with id={}", savedHabit.getId());

        return toResponse(savedHabit);
    }

    public List<HabitResponse> getAll() {
        User user = getCurrentUser();

        log.info("Receive all habits");

        return habitRepository.findAllByUserId(user.getId())
                              .stream()
                              .map(this::toResponse)
                              .toList();
    }

    public HabitResponse getById(Long id) {
        User user = getCurrentUser();

        log.info("Searching habit by id={}", id);

        Habit habit = habitRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new HabitNotFoundException(""));
        return toResponse(habit);
    }

    @Transactional
    public HabitResponse update(Long id, HabitRequest habitRequest) {
        User user = getCurrentUser();

        log.info("Updating habit by id={}", id);

        Habit habit = habitRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new HabitNotFoundException(""));

        habit.setName(habitRequest.getName());
        habit.setDescription(habitRequest.getDescription());
        habit.setTarget(habitRequest.getTarget());

        Habit updatedHabit = habitRepository.save(habit);

        log.info("Habit updated successfully by habit with name={}, description={}, target={}",
                habitRequest.getName(), habitRequest.getDescription(), habitRequest.getTarget());

        return toResponse(updatedHabit);
    }

    @Transactional
    public void delete(Long id) {
        User user = getCurrentUser();

        log.info("Deleting habit by id={}", id);

        Habit habit = habitRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new HabitNotFoundException("Habit not found"));
        habitRepository.delete(habit);

        log.info("Habit by id={} successfully deleted", id);
    }

    private HabitResponse toResponse(Habit habit) {
        return new HabitResponse(habit.getId(),
                                 habit.getName(),
                                 habit.getDescription(),
                                 habit.getTarget());
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }
}
