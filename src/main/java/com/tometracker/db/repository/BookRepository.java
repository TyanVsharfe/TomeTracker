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
    Iterable<Book> findBooksByGenres(List<String> genres);
    @Query("SELECT b FROM Book b JOIN b.genres g WHERE g LIKE %:genre%")
    List<Book> findBooksByGenreContaining(@Param("genre") String genre);
}
