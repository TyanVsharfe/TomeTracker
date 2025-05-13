package com.tometracker.db.repository;

import com.tometracker.db.model.ReadingGoal;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ReadingGoalRepository extends CrudRepository<ReadingGoal, Long> {
    Iterable<ReadingGoal> findAllByUserId(Long userId);
    Optional<ReadingGoal> findByUserId(Long userId);
    void deleteReadingGoalById(Long goalId);
}
