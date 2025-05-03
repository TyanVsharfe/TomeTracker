package com.tometracker.controller;

import com.tometracker.data_template.Enums;
import com.tometracker.db.model.Achievement;
import com.tometracker.db.model.User;
import com.tometracker.db.model.UserAchievement;
import com.tometracker.service.AchievementService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/achievements")
public class AchievementController {

    private final AchievementService achievementService;

    public AchievementController(AchievementService achievementService) {
        this.achievementService = achievementService;
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<Iterable<Achievement>> getAchievementsByCategory(@PathVariable Enums.AchievementCategory category) {
        return ResponseEntity.ok(achievementService.getAchievementsByCategory(category));
    }

    //TODO Переписать все на AuthenticationPrincipal
    @GetMapping("")
    public ResponseEntity<Iterable<UserAchievement>> getUserAchievements(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(achievementService.getUserAchievements(user.getId()));
    }

    @PostMapping
    public ResponseEntity<Achievement> createAchievement(@RequestBody Achievement achievement) {
        return ResponseEntity.ok(achievementService.createAchievement(achievement));
    }
}
