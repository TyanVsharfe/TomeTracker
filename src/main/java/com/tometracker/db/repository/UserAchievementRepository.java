package com.tometracker.db.repository;

import com.tometracker.db.model.User;
import com.tometracker.db.model.UserAchievement;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAchievementRepository extends CrudRepository<UserAchievement, Long> {
    long countByUser(User user);
    List<UserAchievement> findByUserId(Long userId);

    List<UserAchievement> findByUserIdAndCompleted(Long userId, Boolean completed);

    Optional<UserAchievement> findByUserIdAndAchievementId(Long userId, Long achievementId);

    @Query("SELECT COUNT(ua) FROM UserAchievement ua WHERE ua.user.id = :userId AND ua.completed = true")
    Integer countCompletedAchievements(Long userId);
}
