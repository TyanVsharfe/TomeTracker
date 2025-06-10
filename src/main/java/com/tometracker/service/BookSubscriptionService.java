package com.tometracker.service;

import com.tometracker.db.model.Book;
import com.tometracker.db.repository.BookRepository;
import com.tometracker.dto.subscription.SubscriptionDTO;
import org.springframework.beans.factory.annotation.Value;
import com.tometracker.db.model.BookSubscription;
import com.tometracker.db.model.User;
import com.tometracker.db.repository.BookSubscriptionRepository;
import com.tometracker.dto.subscription.PriceCheckRequest;
import com.tometracker.dto.subscription.PriceCheckResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookSubscriptionService {

    private final BookSubscriptionRepository bookSubscriptionRepository;
    private final NotificationService notificationService;
    private final BookRepository bookRepository;

    @Value("${price.parser.service.url}")
    private static String priceParserServiceUrl;

    public BookSubscriptionService(BookSubscriptionRepository bookSubscriptionRepository, NotificationService notificationService, BookRepository bookRepository) {
        this.bookSubscriptionRepository = bookSubscriptionRepository;
        this.notificationService = notificationService;
        this.bookRepository = bookRepository;
    }

    @Transactional
    public void createSubscription(User user, SubscriptionDTO subscriptionDTO) {
        Optional<BookSubscription> subscribe = bookSubscriptionRepository.findByBook_GbIdAndUserAndBookUrlAndStoreName(
                subscriptionDTO.bookId(), user, subscriptionDTO.bookUrl(), subscriptionDTO.storeName());

        if (subscribe.isPresent()) {
            throw new IllegalArgumentException("A subscription for this book already exists in this store");
        }

        Optional<Book> book = bookRepository.findBookByGbId(subscriptionDTO.bookId());
        if (book.isEmpty()) {
            throw new IllegalArgumentException("This book ID is incorrect");
        }

        BookSubscription subscription = new BookSubscription(user, book.get(),
                subscriptionDTO.bookUrl(), subscriptionDTO.storeName(), subscriptionDTO.targetPrice());

        bookSubscriptionRepository.save(subscription);

        notificationService.createPriceNotifications(subscription, null, null);
    }

    @Transactional
    public void deactivateSubscription(Long subscriptionId, Long userId) {
        BookSubscription subscription = bookSubscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));

        if (!subscription.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("There are no permissions to change this subscription");
        }

        bookSubscriptionRepository.delete(subscription);
    }

    public List<BookSubscription> getSubscriptionsByUser(User user) {
        return bookSubscriptionRepository.findByUser(user);
    }

    @Transactional
    public void processDailyPriceCheck() {

        List<BookSubscription> activeSubscriptions = bookSubscriptionRepository.findAllActiveOrderByLastCheck();

        if (activeSubscriptions.isEmpty()) {
            return;
        }

        List<PriceCheckRequest.BookUrlData> bookUrls = activeSubscriptions.stream()
                .map(subscription -> new PriceCheckRequest.BookUrlData(
                        subscription.getId(),
                        subscription.getBookUrl(),
                        subscription.getStoreName(),
                        subscription.getUser().getId(),
                        subscription.getBook().getTitle()
                ))
                .collect(Collectors.toList());

        PriceCheckRequest request = new PriceCheckRequest(bookUrls, LocalDateTime.now().toInstant(ZoneOffset.UTC));

        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<PriceCheckResponse> response = restTemplate.postForEntity(
                    priceParserServiceUrl + "/check-prices",
                    request,
                    PriceCheckResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                processPriceCheckResponse(response.getBody());
            }

        } catch (Exception e) {
        }
    }

    @Transactional
    protected void processPriceCheckResponse(PriceCheckResponse response) {
        for (PriceCheckResponse.BookPriceData priceData : response.getPriceData()) {
            try {
                BookSubscription subscription = bookSubscriptionRepository.findById(priceData.getSubscriptionId())
                        .orElse(null);

                if (subscription == null) {
                    continue;
                }

                BigDecimal oldPrice = subscription.getLastCheckedPrice();
                BigDecimal newPrice = priceData.getCurrentPrice();

                subscription.setLastCheckedPrice(newPrice);
                subscription.setLastPriceCheck(Instant.from(LocalDateTime.now()));
                bookSubscriptionRepository.save(subscription);

                notificationService.createPriceNotifications(subscription, oldPrice, newPrice);
            } catch (Exception e) {
            }
        }
    }
}