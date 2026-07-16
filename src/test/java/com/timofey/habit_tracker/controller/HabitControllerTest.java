package com.timofey.habit_tracker.controller;

import com.timofey.habit_tracker.dto.HabitResponse;
import com.timofey.habit_tracker.exception.HabitNotFoundException;
import com.timofey.habit_tracker.service.HabitService;
import com.timofey.habit_tracker.service.JwtService;
import com.timofey.habit_tracker.service.StatsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HabitController.class)
@AutoConfigureMockMvc(addFilters = false)
class HabitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HabitService habitService;

    @MockitoBean
    private StatsService statsService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void getById_whenExists_returns200() throws Exception {
        HabitResponse response = new HabitResponse(
                1L,
                "Пить воду",
                "2 литра",
                7
        );

        when(habitService.getById(1L))
                .thenReturn(response);

        mockMvc.perform(get("/api/habits/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name")
                        .value("Пить воду"));
    }


    @Test
    void getById_whenNotExists_returns404() throws Exception {
        when(habitService.getById(99L))
                .thenThrow(new HabitNotFoundException("Habit not found"));

        mockMvc.perform(get("/api/habits/99"))
                .andExpect(status().isNotFound());
    }


    @Test
    void create_withValidBody_returns201() throws Exception {
        HabitResponse response = new HabitResponse(
                1L,
                "Пить воду",
                "2 литра",
                7
        );

        when(habitService.create(any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/habits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Пить воду",
                                    "description": "2 литра",
                                    "target": 7
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id")
                        .value(1L));
    }
}
