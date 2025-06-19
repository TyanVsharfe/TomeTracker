package com.tometracker.db.repository;

import com.tometracker.db.model.Book;
import com.tometracker.db.model.BookSubscription;
import com.tometracker.db.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookSubscriptionRepository extends CrudRepository<BookSubscription, Long> {
    List<BookSubscription> findByBookAndUser(Book book, User user);
    List<BookSubscription> findByUser(User user);
    Optional<BookSubscription> findByBook_GbIdAndUserAndBookUrlAndStoreName(String gbId, User user, String bookUrl, String storeName);

    @Query("SELECT bs FROM BookSubscription bs " +
            "ORDER BY bs.lastPriceCheck ASC NULLS FIRST")
    List<BookSubscription> findAllActiveOrderByLastCheck();
}
