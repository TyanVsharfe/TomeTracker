package com.tometracker.dto;

import com.tometracker.data_template.Enums;

import java.time.Instant;

public record UserAchievementDTO(Long id, String name, String description, Enums.AchievementCategory category,
                                 int requiredCount, int currentProgress, boolean completed, Instant achievedAt,
                                 String iconUrl, int experiencePoints) {
}
