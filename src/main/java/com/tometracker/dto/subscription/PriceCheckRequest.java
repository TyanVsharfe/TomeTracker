package com.tometracker.dto.subscription;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceCheckRequest {
    private List<BookUrlData> bookUrls;
    private Instant requestTime;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookUrlData {
        private Long subscriptionId;
        private String url;
        private String storeName;
        private Long userId;
        private String bookTitle;
    }
}
