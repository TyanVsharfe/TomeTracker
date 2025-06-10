package com.tometracker.service;

import com.tometracker.db.model.BookSubscription;
import com.tometracker.db.model.PriceNotification;
import com.tometracker.db.model.User;
import com.tometracker.db.repository.PriceNotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class NotificationService {
    private final PriceNotificationRepository priceNotificationRepository;

    public NotificationService(PriceNotificationRepository priceNotificationRepository) {
        this.priceNotificationRepository = priceNotificationRepository;
    }

    @Transactional
    protected void createPriceNotifications(BookSubscription subscription, BigDecimal oldPrice, BigDecimal newPrice) {
        User user = subscription.getUser();

        PriceNotification.NotificationType notificationType;
        String message;

        if (oldPrice == null && newPrice == null) {
            notificationType = PriceNotification.NotificationType.FIRST_CHECK;
            message = String.format("Вы подписались на отслеживание цены книги \"%s\" в магазине %s. Целевая цена: %.2f",
                    subscription.getBook().getTitle(),
                    subscription.getStoreName(),
                    subscription.getTargetPrice());

            PriceNotification notification = new PriceNotification(
                    user, subscription, notificationType, null, subscription.getTargetPrice(), message);

            priceNotificationRepository.save(notification);
            return;
        }

        if (newPrice == null) {
            return;
        }

        if (newPrice.compareTo(oldPrice) < 0) {
            notificationType = PriceNotification.NotificationType.PRICE_DROP;
            BigDecimal decrease = oldPrice.subtract(newPrice);
            message = String.format("Цена на книгу \"%s\" снизилась на %.2f (с %.2f до %.2f) в магазине %s",
                    subscription.getBook().getTitle(),
                    decrease, oldPrice, newPrice,
                    subscription.getStoreName());
        } else if (newPrice.compareTo(oldPrice) > 0) {
            notificationType = PriceNotification.NotificationType.PRICE_RISE;
            BigDecimal increase = newPrice.subtract(oldPrice);
            message = String.format("Цена на книгу \"%s\" выросла на %.2f (с %.2f до %.2f) в магазине %s",
                    subscription.getBook().getTitle(),
                    increase, oldPrice, newPrice,
                    subscription.getStoreName());
        } else {
            return;
        }

        if (subscription.getTargetPrice() != null &&
                newPrice.compareTo(subscription.getTargetPrice()) <= 0) {
            notificationType = PriceNotification.NotificationType.TARGET_REACHED;
            message = String.format("Целевая цена достигнута! Книга \"%s\" теперь стоит %.2f в магазине %s",
                    subscription.getBook().getTitle(),
                    newPrice,
                    subscription.getStoreName());
        }

        PriceNotification notification = new PriceNotification(
                user, subscription, notificationType, oldPrice, newPrice, message);

        priceNotificationRepository.save(notification);
    }

    public List<PriceNotification> getUserNotifications(User user, Pageable pageable) {
        return priceNotificationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }

    public List<PriceNotification> getUserNotifications(User user, Boolean isRead, Pageable pageable) {
        return priceNotificationRepository.findByUserAndIsReadOrderByCreatedAtDesc(user, isRead, pageable);
    }

    @Transactional
    public void markNotificationAsRead(Long notificationId, User user) {
        priceNotificationRepository.markAsReadById(notificationId, user, LocalDateTime.now().toInstant(ZoneOffset.UTC));
    }

    @Transactional
    public void deleteNotification(Long notificationId, User user) {

        PriceNotification priceNotification = priceNotificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

        if (!priceNotification.getUser().equals(user)) {
            throw new IllegalArgumentException("There are no permissions to delete this notification");
        }

        priceNotificationRepository.delete(priceNotification);
    }

    @Transactional
    public void markAllNotificationsAsRead(User user) {
        priceNotificationRepository.markAllAsReadByUser(user, Instant.from(LocalDateTime.now()));
    }
}
