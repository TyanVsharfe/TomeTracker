package com.tometracker.controller;

import com.tometracker.db.model.Book;
import com.tometracker.db.model.User;
import com.tometracker.service.BookAnalyticsService;
import com.tometracker.service.RecommendationService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recommendations")
public class RecommendationController {
    private final RecommendationService recommendationService;
    private final BookAnalyticsService bookAnalyticsService;

    public RecommendationController(RecommendationService recommendationService, BookAnalyticsService bookAnalyticsService) {
        this.recommendationService = recommendationService;
        this.bookAnalyticsService = bookAnalyticsService;
    }

    @GetMapping("/content-based")
    public List<Book> getContentBasedRecommendations(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "20") int limit) {

        return recommendationService.getRecommendationsForUser(user.getId(), limit);
    }

    @GetMapping("/populars")
    public List<Book> getPopular(@AuthenticationPrincipal User user,
                                 @RequestParam(defaultValue = "30") int limit) {

        return bookAnalyticsService.getPopularBooks(limit);
    }

    @GetMapping("/genre/{genre}")
    public Iterable<Book> getRecommendationByGenre(@PathVariable("genre") String genre) {
        return recommendationService.getBooksByGenre(genre);
    }
}
