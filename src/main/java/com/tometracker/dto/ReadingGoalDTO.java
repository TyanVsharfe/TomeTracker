package com.tometracker.dto;

import com.tometracker.db.model.ReadingGoal;

import java.time.Instant;

public record ReadingGoalDTO(String title, String description, int targetBooks, Instant startDate,
                             Instant endDate, ReadingGoal.GoalType goalType) {
}
