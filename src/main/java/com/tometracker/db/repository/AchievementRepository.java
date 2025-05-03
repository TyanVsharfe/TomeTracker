package com.tometracker.db.repository;

import com.tometracker.data_template.Enums;
import com.tometracker.db.model.Achievement;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AchievementRepository extends CrudRepository<Achievement, Long> {
    List<Achievement> findByCategory(Enums.AchievementCategory category);
}
