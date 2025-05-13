package com.tometracker.service;

import com.tometracker.db.model.ReadingGoal;
import com.tometracker.db.model.User;
import com.tometracker.db.repository.ReadingGoalRepository;
import com.tometracker.db.repository.UserRepository;
import com.tometracker.dto.ReadingGoalDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ReadingGoalService {

    private final ReadingGoalRepository readingGoalRepository;
    private final UserRepository userRepository;

    public ReadingGoalService(ReadingGoalRepository readingGoalRepository, UserRepository userRepository) {
        this.readingGoalRepository = readingGoalRepository;
        this.userRepository = userRepository;
    }

    public Iterable<ReadingGoal> getGoalByUserId(Long userId) {
        return readingGoalRepository.findAllByUserId(userId);
    }

    @Transactional
    public void deleteGoalById(Long goalId) {
        readingGoalRepository.deleteReadingGoalById(goalId);
    }

    public ReadingGoal updateGoal(Long goalId, ReadingGoal updatedGoal) {
        return readingGoalRepository.findById(goalId).map(goal -> {
            goal.setTitle(updatedGoal.getTitle());
            goal.setTargetBooks(updatedGoal.getTargetBooks());
            goal.setStartDate(updatedGoal.getStartDate());
            goal.setEndDate(updatedGoal.getEndDate());
            goal.setDescription(updatedGoal.getDescription());
            goal.setGoalType(updatedGoal.getGoalType());
            goal.setCompleted(updatedGoal.getCompleted());
            return readingGoalRepository.save(goal);
        }).orElseThrow(() -> new RuntimeException("Goal not found"));
    }

    public ReadingGoal createGoal(ReadingGoalDTO request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ReadingGoal goal = new ReadingGoal(
                user,
                request.title(),
                request.targetBooks(),
                request.startDate(),
                request.endDate(),
                request.goalType()
        );
        goal.setDescription(request.description());

        return readingGoalRepository.save(goal);
    }
}
