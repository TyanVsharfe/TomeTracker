package com.tometracker.db.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "book_subscriptions")
@NoArgsConstructor
public class BookSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false, length = 1000)
    private String bookUrl;

    @Column(nullable = false, length = 100)
    private String storeName;

    @Column(name = "target_price", precision = 10, scale = 2)
    private BigDecimal targetPrice;

    @Column(name = "last_checked_price", precision = 10, scale = 2)
    private BigDecimal lastCheckedPrice;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "last_price_check")
    private Instant lastPriceCheck;

    @JsonBackReference
    @OneToMany(mappedBy = "bookSubscription")
    private List<PriceNotification> notifications;

    public BookSubscription(User user, Book book, String bookUrl, String storeName, BigDecimal targetPrice) {
        this.user = user;
        this.book = book;
        this.bookUrl = bookUrl;
        this.storeName = storeName;
        this.targetPrice = targetPrice;
        this.createdAt = Instant.now();
    }
}