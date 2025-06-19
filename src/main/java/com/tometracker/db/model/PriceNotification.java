package com.tometracker.db.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Getter
@Setter
@Entity
@Table(name = "price_notifications")
@NoArgsConstructor
public class PriceNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "book_subscription_id", nullable = false)
    private BookSubscription bookSubscription;

    @Column(name = "notification_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @Column(name = "old_price", precision = 10, scale = 2)
    private BigDecimal oldPrice;

    @Column(name = "new_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal newPrice;

    @Column(name = "price_difference", precision = 10, scale = 2)
    private BigDecimal priceDifference;

    @Column(length = 500)
    private String message;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "read_at")
    private Instant readAt;

    public enum NotificationType {
        PRICE_DROP,
        PRICE_RISE,
        TARGET_REACHED,
        FIRST_CHECK
    }

    public PriceNotification(User user, BookSubscription bookSubscription,
                             NotificationType notificationType, BigDecimal oldPrice,
                             BigDecimal newPrice, String message) {
        this.user = user;
        this.bookSubscription = bookSubscription;
        this.notificationType = notificationType;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
        this.message = message;
        this.createdAt = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        this.isRead = false;

        if (oldPrice != null && newPrice != null) {
            this.priceDifference = newPrice.subtract(oldPrice);
        }
    }
}
