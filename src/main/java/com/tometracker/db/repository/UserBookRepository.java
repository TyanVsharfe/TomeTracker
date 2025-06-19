package com.tometracker.db.repository;

import com.tometracker.data_template.Enums;
import com.tometracker.db.model.Author;
import com.tometracker.db.model.Book;
import com.tometracker.db.model.UserBook;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBookRepository extends CrudRepository<UserBook, Long> {
    Optional<UserBook> findByBook_GbIdAndUser_Username(String gbId, String username);
    Optional<UserBook> findByUserIdAndBookGbId(Long userId, String bookId);
    void deleteBookByBook_GbIdAndUser_Username(String gbId, String username);
    boolean existsBookByBook_GbIdAndUser_Username(String gbId, String username);
    Iterable<UserBook> findBooksByStatusAndUser_Username(Enums.status status, String username);
    Iterable<UserBook> findBooksByUser_Username(String username);
    long countBooksByStatusAndUser_Username(Enums.status status, String username);
    long countAllByUser_Username(String username);
    long countByUser_UsernameAndBook_GenresContaining(String username, String genre);
    long countByUserIdAndStatus(Long userId, Enums.status status);
    List<UserBook> findByBookGbIdAndReviewIsNotNull(String gbId);
    List<UserBook> findByUserIdAndStatus(Long userId, Enums.status status);
    List<UserBook> findByUserId(Long userId);

    @Query("SELECT ub FROM UserBook ub WHERE ub.book.gbId = :bookId AND ub.userRating >= 70")
    List<UserBook> findUsersWhoLikedBook(@Param("bookId") String bookId);

    @Query("SELECT AVG(ub.userRating) FROM UserBook ub WHERE ub.book.gbId = :bookId AND ub.userRating IS NOT NULL")
    Double getAverageRatingForBook(@Param("bookId") String bookId);

    @Query("SELECT COUNT(ub) FROM UserBook ub WHERE ub.book.gbId = :bookId")
    long countByBookGbId(@Param("bookId") String bookId);

    @Query("SELECT ub.book FROM UserBook ub WHERE ub.status = :status AND ub.user.username = :username")
    Iterable<Book> findBooksByStatusAndUserUsername(Enums.status status, String username);

    @Query("SELECT ub.book FROM UserBook ub WHERE ub.user.username = :username")
    List<Book> findAllBooksByUserUsername(String username);

    @Query("SELECT DISTINCT b.genres FROM UserBook ub JOIN ub.book b WHERE ub.user.username = :username")
    List<String> findDistinctGenresByUsername(String username);

    @Query("SELECT DISTINCT b.authors FROM UserBook ub JOIN ub.book b WHERE ub.user.username = :username")
    List<Author> findDistinctAuthorsByUsername(String username);

    @Query("SELECT COUNT(DISTINCT b.genres) FROM UserBook ub JOIN ub.book b WHERE ub.user.id = :userId AND ub.status = :status")
    long countDistinctGenresByUserIdAndStatus(Long userId, Enums.status status);

    @Query("SELECT COUNT(DISTINCT a) FROM UserBook ub JOIN ub.book b JOIN b.authors a WHERE ub.user.id = :userId AND ub.status = :status")
    long countDistinctAuthorsByUserIdAndStatus(Long userId, Enums.status status);
}
