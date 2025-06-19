package com.tometracker.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PriceCheckScheduler {

    private final BookSubscriptionService bookSubscriptionService;

    public PriceCheckScheduler(BookSubscriptionService bookSubscriptionService) {
        this.bookSubscriptionService = bookSubscriptionService;
    }

    @Scheduled(cron = "0 0 2 * * ?")
    public void scheduleDailyPriceCheck() {
        try {
            bookSubscriptionService.processDailyPriceCheck();
        } catch (Exception e) {
        }
    }

    //@Scheduled(fixedDelay = 86400000, initialDelay = 60000)
    public void scheduleFixedDelayPriceCheck() {
        try {
            bookSubscriptionService.processDailyPriceCheck();
        } catch (Exception e) {
        }
    }
}
