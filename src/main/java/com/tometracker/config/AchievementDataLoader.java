package com.tometracker.config;

import com.tometracker.data_template.Enums;
import com.tometracker.db.model.Achievement;
import com.tometracker.db.model.User;
import com.tometracker.db.model.UserAchievement;
import com.tometracker.db.repository.AchievementRepository;
import com.tometracker.db.repository.UserAchievementRepository;
import com.tometracker.db.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class AchievementDataLoader {
    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final UserRepository userRepository;

    public AchievementDataLoader(AchievementRepository achievementRepository, UserAchievementRepository userAchievementRepository, UserRepository userRepository) {
        this.achievementRepository = achievementRepository;
        this.userAchievementRepository = userAchievementRepository;
        this.userRepository = userRepository;
    }
//
//    @Bean
//    public CommandLineRunner loadAchievementData() {
//        return args -> {
//            List<Achievement> totalBooksAchievements = Arrays.asList(
//                    new Achievement("Начинающий читатель", "Прочитайте 5 книг",
//                            Enums.AchievementCategory.TOTAL_BOOKS_READ, 5, "/icons/beginner-reader.png", 10),
//                    new Achievement("Книжный червь", "Прочитайте 15 книг",
//                            Enums.AchievementCategory.TOTAL_BOOKS_READ, 10, "/icons/bookworm.png", 25),
//                    new Achievement("Библиофил", "Прочитайте 50 книг",
//                            Enums.AchievementCategory.TOTAL_BOOKS_READ, 50, "/icons/bibliophile.png", 100),
//                    new Achievement("Мастер чтения", "Прочитайте 100 книг",
//                            Enums.AchievementCategory.TOTAL_BOOKS_READ, 100, "/icons/reading-master.png", 250)
//            );
//
//            List<Achievement> genreAchievements = Arrays.asList(
//                    new Achievement("Fantasy Читатель - 10", "Прочитайте 10 книг в жанре фэнтези",
//                            Enums.AchievementCategory.GENRE_SPECIFIC, 10, "/icons/fantasy.png", 20),
//                    new Achievement("Economy Читатель - 10", "Прочитайте 10 книг по экономике",
//                            Enums.AchievementCategory.GENRE_SPECIFIC, 10, "/icons/mystery.png", 20),
//                    new Achievement("Sci-Fi Читатель - 10", "Прочитайте 10 научно-фантастических книг",
//                            Enums.AchievementCategory.GENRE_SPECIFIC, 10, "/icons/scifi.png", 20)
//            );
//
//            List<Achievement> authorAchievements = Arrays.asList(
//                    new Achievement("Фанат Толкина", "Прочитайте 3 книги Дж. Р. Р. Толкина",
//                            Enums.AchievementCategory.AUTHOR_SPECIFIC, 3, "/icons/tolkien.png", 15),
//                    new Achievement("Фанат Стивена кинга", "Прочитайте 5 книг Стивена Кинга",
//                            Enums.AchievementCategory.AUTHOR_SPECIFIC, 5, "/icons/king.png", 25)
//            );
//
//            List<Achievement> pagesAchievements = Arrays.asList(
//                    new Achievement("1,000 Страниц", "Прочитайте в общей сложности 1,000 страниц",
//                            Enums.AchievementCategory.PAGES_READ, 1000, "/icons/1000-pages.png", 20),
//                    new Achievement("5,000 Страниц", "Прочитайте в общей сложности 5,000 страниц",
//                            Enums.AchievementCategory.PAGES_READ, 5000, "/icons/5000-pages.png", 50),
//                    new Achievement("10,000 Страниц", "Прочитайте в общей сложности 10,000 страниц",
//                            Enums.AchievementCategory.PAGES_READ, 10000, "/icons/10000-pages.png", 100)
//            );
//
//            List<Achievement> allAchievements = new ArrayList<>();
//            allAchievements.addAll(totalBooksAchievements);
//            allAchievements.addAll(genreAchievements);
//            allAchievements.addAll(authorAchievements);
//            allAchievements.addAll(pagesAchievements);
//
//            achievementRepository.saveAll(allAchievements);
//        };
//    }

//    @Bean
//    public CommandLineRunner initializeAchievementsForAllUsers() {
//        return args -> {
//            List<User> allUsers = (List<User>) userRepository.findAll();
//            List<Achievement> allAchievements = (List<Achievement>) achievementRepository.findAll();
//
//            for (User user : allUsers) {
//                if (userAchievementRepository.countByUser(user) == 0) {
//                    List<UserAchievement> userAchievements = allAchievements.stream()
//                            .map(achievement -> new UserAchievement(user, achievement))
//                            .collect(Collectors.toList());
//
//                    userAchievementRepository.saveAll(userAchievements);
//                }
//            }
//        };
//    }
}
