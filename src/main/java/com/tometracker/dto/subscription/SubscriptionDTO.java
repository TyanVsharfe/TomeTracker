package com.tometracker.dto.subscription;

import java.math.BigDecimal;

public record SubscriptionDTO(String bookId, String bookUrl, String storeName, BigDecimal targetPrice) {
}
