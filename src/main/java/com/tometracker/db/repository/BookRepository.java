package com.tometracker.db.repository;

import com.tometracker.data_template.Enums;
import com.tometracker.db.model.Book;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends CrudRepository<Book, Long> {
    void deleteBookByGbId(String gb_id);
    Optional<Book> findBookByGbId(String gb_id);
    Iterable<Book> findBooksByStatus(Enums.status status);
}
