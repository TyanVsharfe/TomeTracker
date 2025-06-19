package com.tometracker.db.repository;

import com.tometracker.data_template.Enums;
import com.tometracker.db.model.Book;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends CrudRepository<Book, Long> {
    void deleteBookByGbId(String gb_id);
    boolean existsBookByGbId(String gId);
    Optional<Book> findBookByGbId(String gb_id);
    List<Book> findByPublisher(String publisher);
    List<Book> findByTitleContainingIgnoreCase(String titleKeyword);

    @Query("SELECT b FROM Book b JOIN b.genres g WHERE g = :genre")
    List<Book> findByGenre(@Param("genre") String genre);

    @Query("SELECT b FROM Book b JOIN b.authors a WHERE a.name LIKE %:authorName%")
    List<Book> findByAuthorName(@Param("authorName") String authorName);

    @Query("SELECT b FROM Book b WHERE b.pageCount BETWEEN :minPages AND :maxPages")
    List<Book> findWithinPageCountRange(@Param("minPages") int minPages, @Param("maxPages") int maxPages);

    @Query("SELECT b FROM Book b JOIN b.genres g WHERE g LIKE %:genre%")
    List<Book> findBooksByGenreContaining(@Param("genre") String genre);
}
