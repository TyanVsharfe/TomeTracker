package com.tometracker.dto.subscription;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceCheckResponse {
    private List<BookPriceData> priceData;
    private Instant responseTime;
    private String status;
    private String errorMessage;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookPriceData {
        private Long subscriptionId;
        private String url;
        private BigDecimal currentPrice;
        private String currency;
        private boolean available;
        private String errorMessage;
        private Instant checkedAt;
    }
}
