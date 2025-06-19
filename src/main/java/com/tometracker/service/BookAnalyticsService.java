package com.tometracker.service;

import com.tometracker.db.model.Book;
import com.tometracker.db.repository.BookRepository;
import com.tometracker.db.repository.UserBookRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookAnalyticsService {

        private final UserBookRepository userBookRepository;
        private final BookRepository bookRepository;

    private List<Book> popularBooks = new ArrayList<>();
    private final Map<String, List<Book>> popularBooksByGenre = new HashMap<>();

    public BookAnalyticsService(UserBookRepository userBookRepository, BookRepository bookRepository) {
        this.userBookRepository = userBookRepository;
        this.bookRepository = bookRepository;
    }

    public List<Book> getPopularBooks(int limit) {
        if (popularBooks.isEmpty()) {
            updatePopularBooks();
        }

        return popularBooks.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<Book> getPopularBooksByGenre(String genre, int limit) {
        if (popularBooksByGenre.isEmpty() || !popularBooksByGenre.containsKey(genre)) {
            updatePopularBooksByGenre();
        }

        List<Book> genreBooks = popularBooksByGenre.getOrDefault(genre, Collections.emptyList());

        return genreBooks.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
    public void updatePopularBooks() {
        Iterable<Book> allBooks = bookRepository.findAll();

        Map<Book, Double> bookPopularity = new HashMap<>();

        for (Book book : allBooks) {
            long userCount = userBookRepository.countByBookGbId(book.getGbId());

            Double avgRating = userBookRepository.getAverageRatingForBook(book.getGbId());

            double popularityScore = userCount * (avgRating != null ? avgRating : 50);

            bookPopularity.put(book, popularityScore);
        }

        popularBooks = bookPopularity.entrySet().stream()
                .sorted(Map.Entry.<Book, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
    public void updatePopularBooksByGenre() {
        popularBooksByGenre.clear();

        if (popularBooks.isEmpty()) {
            updatePopularBooks();
        }

        for (Book book : popularBooks) {
            if (book.getGenres() != null) {
                for (String genre : book.getGenres()) {
                    popularBooksByGenre.computeIfAbsent(genre, k -> new ArrayList<>()).add(book);
                }
            }
        }
    }
}
