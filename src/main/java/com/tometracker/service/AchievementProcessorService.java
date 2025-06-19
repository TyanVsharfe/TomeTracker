package com.tometracker.service;

import com.tometracker.data_template.Enums;
import com.tometracker.db.model.Achievement;
import com.tometracker.db.model.User;
import com.tometracker.db.model.UserBook;
import com.tometracker.db.repository.UserBookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AchievementProcessorService {
    private final UserBookRepository userBookRepository;
    private final AchievementService achievementService;

    public AchievementProcessorService(UserBookRepository userBookRepository, AchievementService achievementService) {
        this.userBookRepository = userBookRepository;
        this.achievementService = achievementService;
    }

    @Transactional(readOnly = true)
    public void checkTotalBooksRead(User user) {
        long totalRead = userBookRepository.countByUserIdAndStatus(user.getId(), Enums.status.Completed);

        Iterable<Achievement> totalBooksAchievements = achievementService.getAchievementsByCategory(Enums.AchievementCategory.TOTAL_BOOKS_READ);

        for (Achievement achievement : totalBooksAchievements) {
            achievementService.updateAchievementProgress(user.getId(), achievement.getId(), (int) totalRead);
        }
    }

    @Transactional(readOnly = true)
    public void checkGenreAchievements(User user) {
        List<UserBook> readBooks = userBookRepository.findByUserIdAndStatus(user.getId(), Enums.status.Completed);

        Map<String, Integer> genreCounts = new HashMap<>();

        for (UserBook userBook : readBooks) {
            List<String> genres = userBook.getBook().getGenres();
            if (genres != null) {
                for (String genre : genres) {
                    genreCounts.put(genre, genreCounts.getOrDefault(genre, 0) + 1);
                }
            }
        }

        Iterable<Achievement> genreAchievements = achievementService.getAchievementsByCategory(Enums.AchievementCategory.GENRE_SPECIFIC);

        for (Achievement achievement : genreAchievements) {
            String genre = extractGenreFromAchievementName(achievement.getName());

            genre = switch (genre) {
                case "Фэнтези" -> "Fantasy";
                case "Экономика" -> "Business & Economics";
                case "Научная фантастика" -> "Science Fiction";
                default -> genre;
            };

            String finalGenre = genre;
            int count = genreCounts.entrySet().stream()
                    .filter(entry -> entry.getKey().contains(finalGenre))
                    .map(Map.Entry::getValue)
                    .reduce(0, Integer::sum);

            achievementService.updateAchievementProgress(user.getId(), achievement.getId(), count);
        }
    }

    private String extractGenreFromAchievementName(String achievementName) {
        if (achievementName.startsWith("Читатель: ")) {
            return achievementName.substring(10);
        }
        return achievementName;
    }

    @Transactional(readOnly = true)
    public void checkAuthorAchievements(User user) {
        List<UserBook> readBooks = userBookRepository.findByUserIdAndStatus(user.getId(), Enums.status.Completed);

        Map<String, Integer> authorCounts = new HashMap<>();

        for (UserBook userBook : readBooks) {
            List<String> authors = userBook.getBook().getAuthors().stream()
                    .map(author -> author.getName())
                    .collect(Collectors.toList());

            if (authors != null) {
                for (String author : authors) {
                    authorCounts.put(author, authorCounts.getOrDefault(author, 0) + 1);
                }
            }
        }

        Iterable<Achievement> authorAchievements = achievementService.getAchievementsByCategory(Enums.AchievementCategory.AUTHOR_SPECIFIC);

        for (Achievement achievement : authorAchievements) {
            String author = extractAuthorFromAchievementName(achievement.getName());
            Integer count = authorCounts.getOrDefault(author, 0);

            achievementService.updateAchievementProgress(user.getId(), achievement.getId(), count);
        }
    }

    private String extractAuthorFromAchievementName(String achievementName) {
        if (achievementName.startsWith("Фанат: ")) {
            return achievementName.substring(7);
        }
        return achievementName;
    }

    @Transactional(readOnly = true)
    public void checkPagesRead(User user) {
        List<UserBook> readBooks = userBookRepository.findByUserIdAndStatus(user.getId(), Enums.status.Completed);

        int totalPages = readBooks.stream()
                .mapToInt(userBook -> userBook.getBook().getPageCount())
                .sum();

        Iterable<Achievement> pagesAchievements = achievementService.getAchievementsByCategory(Enums.AchievementCategory.PAGES_READ);

        for (Achievement achievement : pagesAchievements) {
            achievementService.updateAchievementProgress(user.getId(), achievement.getId(), totalPages);
        }
    }

    @Transactional
    public void processBookCompletion(User user, UserBook userBook) {
        checkTotalBooksRead(user);
        checkGenreAchievements(user);
        checkAuthorAchievements(user);
        checkPagesRead(user);
    }
}
