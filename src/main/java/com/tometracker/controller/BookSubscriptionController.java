package com.tometracker.controller;

import com.tometracker.db.model.BookSubscription;
import com.tometracker.db.model.User;
import com.tometracker.dto.subscription.SubscriptionDTO;
import com.tometracker.service.BookSubscriptionService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books/subscriptions")
public class BookSubscriptionController {
    private final BookSubscriptionService bookSubscriptionService;

    public BookSubscriptionController(BookSubscriptionService bookSubscriptionService) {
        this.bookSubscriptionService = bookSubscriptionService;
    }

    @PostMapping("")
    public void createSubscription(@AuthenticationPrincipal User user, @RequestBody SubscriptionDTO subscriptionDTO) {
        bookSubscriptionService.createSubscription(user, subscriptionDTO);
    }

    @DeleteMapping("")
    public void deleteSubscription(@AuthenticationPrincipal User user, Long subscriptionId) {
        bookSubscriptionService.deactivateSubscription(subscriptionId, user.getId());
    }

    @GetMapping("/all")
    public List<BookSubscription> getSubscriptions(@AuthenticationPrincipal User user) {
        return bookSubscriptionService.getSubscriptionsByUser(user);
    }
}
