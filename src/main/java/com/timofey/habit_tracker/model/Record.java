package com.timofey.habit_tracker.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "records")
@Data
@NoArgsConstructor
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habit_id")
    private Habit habit;

    private LocalDate date;
    private boolean completed;

    public Record(Habit habit, LocalDate date, boolean completed) {
        this.habit = habit;
        this.date = date;
        this.completed = completed;
    }
}
