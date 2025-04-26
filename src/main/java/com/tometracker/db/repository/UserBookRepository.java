package com.tometracker.db.repository;

import com.tometracker.data_template.Enums;
import com.tometracker.db.model.Book;
import com.tometracker.db.model.UserBook;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBookRepository extends CrudRepository<UserBook, Long> {
    Optional<UserBook> findByBook_GbIdAndUser_Username(String gbId, String username);
    void deleteBookByBook_GbIdAndUser_Username(String gbId, String username);
    boolean existsBookByBook_GbIdAndUser_Username(String gbId, String username);
    Iterable<UserBook> findBooksByStatusAndUser_Username(Enums.status status, String username);
    long countBooksByStatusAndUser_Username(Enums.status status, String username);
    long countAllByUser_Username(String username);

    @Query("SELECT ub.book FROM UserBook ub WHERE ub.status = :status AND ub.user.username = :username")
    Iterable<Book> findBooksByStatusAndUserUsername(Enums.status status, String username);

    @Query("SELECT ub.book FROM UserBook ub WHERE ub.user.username = :username")
    List<Book> findAllBooksByUserUsername(String username);
    List<UserBook> findByBookGbIdAndReviewIsNotNull(String gbId);
    @Query("SELECT DISTINCT b.genres FROM UserBook ub JOIN ub.book b WHERE ub.user.username = :username")
    List<String> findDistinctGenresByUsername(String username);
    long countByUser_UsernameAndBook_GenresContaining(String username, String genre);
}
