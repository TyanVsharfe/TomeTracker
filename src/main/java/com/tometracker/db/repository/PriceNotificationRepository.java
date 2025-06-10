package com.tometracker.db.repository;

import com.tometracker.db.model.PriceNotification;
import com.tometracker.db.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Repository
public interface PriceNotificationRepository extends CrudRepository<PriceNotification, Long> {

    List<PriceNotification> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    List<PriceNotification> findByUserAndIsReadOrderByCreatedAtDesc(User user, Boolean isRead, Pageable pageable);

    @Query("SELECT COUNT(pn) FROM PriceNotification pn WHERE pn.user = :user AND pn.isRead = false")
    int countUnreadNotificationsByUser(@Param("user") User user);

    @Modifying
    @Transactional
    @Query("UPDATE PriceNotification pn SET pn.isRead = true, pn.readAt = :readAt WHERE pn.user = :user AND pn.isRead = false")
    int markAllAsReadByUser(@Param("user") User user, @Param("readAt") Instant readAt);

    @Modifying
    @Transactional
    @Query("UPDATE PriceNotification pn SET pn.isRead = true, pn.readAt = :readAt WHERE pn.id = :notificationId AND pn.user = :user")
    int markAsReadById(@Param("notificationId") Long notificationId, @Param("user") User user, @Param("readAt") Instant readAt);

    List<PriceNotification> findByUserAndCreatedAtAfterOrderByCreatedAtDesc(User user, Instant after);
}