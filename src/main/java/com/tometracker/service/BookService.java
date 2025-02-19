package com.tometracker.service;

import com.tometracker.db.model.Book;
import com.tometracker.db.repository.BookRepository;
import com.tometracker.dto.BookDTO;
import com.tometracker.dto.BookUpdateDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Iterable<Book> getAll() {
        return bookRepository.findAll();
    }

    public void get(String gbId) {
        bookRepository.findBookByGbId((gbId));
    }

    public Book add(BookDTO bookDTO) {
        return bookRepository.save(new Book(bookDTO));
    }

    public void update(String gbId, BookUpdateDTO bookUpdateDTO) {
        Book book = bookRepository.findBookByGbId(gbId).orElseThrow(
                () -> new EntityNotFoundException("Game with id " + bookUpdateDTO.gbId() + " not found"));
        book.setStatus(bookUpdateDTO.status().orElse(book.getStatus()));
        book.setUserRating(bookUpdateDTO.userRating().orElse(book.getUserRating()));

        if (bookUpdateDTO.notes().isPresent()) {
            bookUpdateDTO.notes().ifPresent(notes -> {
                notes.forEach(note -> {
                    note.setBook(book);
                    book.getNotes().add(note);
                });
            });
        }

        System.out.println("Запись изменена");
        bookRepository.save(book);
    }

    @Transactional
    public void delete(String gbId) {
        bookRepository.deleteBookByGbId(gbId);
    }
}
