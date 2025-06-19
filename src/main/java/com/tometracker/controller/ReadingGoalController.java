package com.tometracker.controller;

import com.tometracker.db.model.ReadingGoal;
import com.tometracker.db.model.User;
import com.tometracker.dto.ReadingGoalDTO;
import com.tometracker.service.ReadingGoalService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/goals")
public class ReadingGoalController {
    private final ReadingGoalService readingGoalService;

    public ReadingGoalController(ReadingGoalService readingGoalService) {
        this.readingGoalService = readingGoalService;
    }

    @GetMapping
    public Iterable<ReadingGoal> getGoal(@AuthenticationPrincipal User user) {
        return readingGoalService.getGoalByUserId(user.getId());
    }

    @DeleteMapping ("/{goalId}")
    public void deleteGoal(@PathVariable Long goalId, @AuthenticationPrincipal User user) {
        readingGoalService.deleteGoalById(goalId);
    }

    @PutMapping("/{goalId}")
    public ResponseEntity<ReadingGoal> updateGoal(@PathVariable Long goalId, @RequestBody ReadingGoal updatedGoal) {
        try {
            ReadingGoal savedGoal = readingGoalService.updateGoal(goalId, updatedGoal);
            return ResponseEntity.ok(savedGoal);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<ReadingGoal> createGoal(@RequestBody ReadingGoalDTO request, @AuthenticationPrincipal User user) {
        try {
            ReadingGoal createdGoal = readingGoalService.createGoal(request, user.getId());
            return ResponseEntity.ok(createdGoal);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

}
