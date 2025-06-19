package com.tometracker.service;

import com.tometracker.data_template.Enums;
import com.tometracker.db.model.Book;
import com.tometracker.db.model.UserBook;
import com.tometracker.db.repository.BookRepository;
import com.tometracker.db.repository.UserBookRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final UserBookRepository userBookRepository;
    private final BookRepository bookRepository;

    public RecommendationService(UserBookRepository userBookRepository, BookRepository bookRepository) {
        this.userBookRepository = userBookRepository;
        this.bookRepository = bookRepository;
    }

    /**
     * Gets book recommendations for a specific user
     * @param userId User ID
     * @param limit maximum number of recommendations
     * @return List of recommended books
     */
    public List<Book> getRecommendationsForUser(Long userId, int limit) {
        List<UserBook> userBooks = userBookRepository.findByUserId(userId);

        if (userBooks.isEmpty()) {
            return getPopularBooks(limit);
        }

        Map<Book, Double> bookScores = calculateBookScores(userBooks);

        Set<String> userBookIds = userBooks.stream()
                .map(userBook -> userBook.getBook().getGbId())
                .collect(Collectors.toSet());

        return bookScores.entrySet().stream()
                .filter(entry -> !userBookIds.contains(entry.getKey().getGbId()))
                .sorted(Map.Entry.<Book, Double>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Calculates a relevance score for each potential book based on user preferences
     */
    private Map<Book, Double> calculateBookScores(List<UserBook> userBooks) {
        UserPreferences preferences = analyzeUserPreferences(userBooks);

        Iterable<Book> allBooks = bookRepository.findAll();
        Map<Book, Double> bookScores = new HashMap<>();

        for (Book book : allBooks) {
            double score = 0.0;

            score += calculateGenreSimilarity(book, preferences.favoriteGenres) * 0.5;

            score += calculateAuthorSimilarity(book, preferences.favoriteAuthors) * 0.3;

            score += calculatePublisherSimilarity(book, preferences.favoritePublishers) * 0.1;

            score += calculatePageCountSimilarity(book, preferences.averagePageCount) * 0.05;

            score += calculateMaturityRatingSimilarity(book, preferences.preferredMaturityRating) * 0.05;

            bookScores.put(book, score);
        }

        return bookScores;
    }

    /**
     * Analyzes user's preferences based on his books
     */
    private UserPreferences analyzeUserPreferences(List<UserBook> userBooks) {
        UserPreferences preferences = new UserPreferences();

        Map<String, Integer> genreCounts = new HashMap<>();
        Map<String, Integer> authorCounts = new HashMap<>();
        Map<String, Integer> publisherCounts = new HashMap<>();
        Map<String, Double> genreRatings = new HashMap<>();

        int totalPageCount = 0;
        int bookCount = userBooks.size();

        for (UserBook userBook : userBooks) {
            Book book = userBook.getBook();
            Double rating = userBook.getUserRating();

            if (book.getGenres() != null) {
                for (String genre : book.getGenres()) {
                    genreCounts.merge(genre, 1, Integer::sum);

                    if (rating != null) {
                        genreRatings.merge(genre, rating, Double::sum);
                    }
                }
            }

            if (book.getAuthors() != null) {
                book.getAuthors().forEach(author ->
                        authorCounts.merge(author.getName(), 1, Integer::sum)
                );
            }

            if (book.getPublisher() != null) {
                publisherCounts.merge(book.getPublisher(), 1, Integer::sum);
            }

            totalPageCount += book.getPageCount();

            if (book.getMaturityRating() != null) {
                preferences.maturityRatingCounts.merge(book.getMaturityRating(), 1, Integer::sum);
            }
        }

        Map<String, Double> averageGenreRatings = new HashMap<>();
        for (String genre : genreRatings.keySet()) {
            averageGenreRatings.put(genre, genreRatings.get(genre) / genreCounts.get(genre));
        }

        preferences.favoriteGenres = sortByValueAndGetKeys(genreCounts, 10);
        preferences.genrePreferences = averageGenreRatings;

        preferences.favoriteAuthors = sortByValueAndGetKeys(authorCounts, 5);

        preferences.favoritePublishers = sortByValueAndGetKeys(publisherCounts, 3);

        preferences.averagePageCount = bookCount > 0 ? totalPageCount / bookCount : 0;

        preferences.preferredMaturityRating = preferences.maturityRatingCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        return preferences;
    }

    /**
     * Calculates genre similarities between a book and the user's preferences
     */
    private double calculateGenreSimilarity(Book book, List<String> favoriteGenres) {
        if (book.getGenres() == null || book.getGenres().isEmpty() || favoriteGenres.isEmpty()) {
            return 0.0;
        }

        double score = 0.0;
        int maxPossibleMatches = Math.min(book.getGenres().size(), favoriteGenres.size());

        for (String bookGenre : book.getGenres()) {
            int genreIndex = favoriteGenres.indexOf(bookGenre);
            if (genreIndex != -1) {
                score += 1.0 - (0.7 * genreIndex / favoriteGenres.size());
            }
        }

        return maxPossibleMatches > 0 ? score / maxPossibleMatches : 0.0;
    }

    /**
     * Calculates similarities by author between a book and the user's preferences
     */
    private double calculateAuthorSimilarity(Book book, List<String> favoriteAuthors) {
        if (book.getAuthors() == null || book.getAuthors().isEmpty() || favoriteAuthors.isEmpty()) {
            return 0.0;
        }

        for (var author : book.getAuthors()) {
            if (favoriteAuthors.contains(author.getName())) {
                return 1.0;
            }
        }

        return 0.0;
    }

    /**
     * Calculates similarity by publisher
     */
    private double calculatePublisherSimilarity(Book book, List<String> favoritePublishers) {
        if (book.getPublisher() == null || favoritePublishers.isEmpty()) {
            return 0.0;
        }

        if (favoritePublishers.contains(book.getPublisher())) {
            return 1.0;
        }

        return 0.0;
    }

    /**
     * Calculates similarity by number of pages
     */
    private double calculatePageCountSimilarity(Book book, int averagePageCount) {
        if (averagePageCount <= 0 || book.getPageCount() <= 0) {
            return 0.0;
        }

        double difference = Math.abs(book.getPageCount() - averagePageCount) / (double) averagePageCount;

        return Math.max(0.0, 1.0 - Math.min(1.0, difference));
    }

    /**
     * Calculates similarity by age rating
     */
    private double calculateMaturityRatingSimilarity(Book book, Enums.maturity preferredMaturityRating) {
        if (book.getMaturityRating() == null || preferredMaturityRating == null) {
            return 0.5;
        }

        return book.getMaturityRating() == preferredMaturityRating ? 1.0 : 0.0;
    }

    /**
     * Returns popular books
     */
    private List<Book> getPopularBooks(int limit) {
        Map<Book, Long> bookPopularity = new HashMap<>();

        for (Book book : bookRepository.findAll()) {
            long userCount = userBookRepository.countByBookGbId(book.getGbId());
            bookPopularity.put(book, userCount);
        }

        return bookPopularity.entrySet().stream()
                .sorted(Map.Entry.<Book, Long>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public Iterable<Book> getBooksByGenre(String genre) {
        if (genre == null || genre.isEmpty()) {
            throw new IllegalArgumentException("Genre cannot be null or empty");
        }

        return bookRepository.findBooksByGenreContaining(genre);
    }

    private <K> List<K> sortByValueAndGetKeys(Map<K, Integer> map, int limit) {
        return map.entrySet().stream()
                .sorted(Map.Entry.<K, Integer>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Class for storing user preferences
     */
    private static class UserPreferences {
        List<String> favoriteGenres = new ArrayList<>();
        Map<String, Double> genrePreferences = new HashMap<>();
        List<String> favoriteAuthors = new ArrayList<>();
        List<String> favoritePublishers = new ArrayList<>();
        int averagePageCount = 0;
        Enums.maturity preferredMaturityRating = null;
        Map<Enums.maturity, Integer> maturityRatingCounts = new HashMap<>();
    }
}
