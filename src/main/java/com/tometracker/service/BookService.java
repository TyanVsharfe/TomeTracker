package com.tometracker.service;

import com.tometracker.db.model.Author;
import com.tometracker.db.model.Book;
import com.tometracker.db.model.User;
import com.tometracker.db.repository.BookRepository;
import com.tometracker.db.repository.UserRepository;
import com.tometracker.dto.BookDTO;
import com.tometracker.dto.UserBookUpdateDTO;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public BookService(BookRepository bookRepository, UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public Iterable<Book> getAll() {
        return bookRepository.findAll();
    }

    public Optional<Book> get(String gbId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication in /get: " + authentication.getName());
        Optional<User> author = userRepository.findByUsername(authentication.getName());
        if (author.isEmpty())
            throw new UsernameNotFoundException("");
        return bookRepository.findBookByGbId((gbId));
    }

    public Book add(BookDTO bookDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication in /add: " + authentication.getName());
        Optional<User> user = userRepository.findByUsername(authentication.getName());
        if (user.isEmpty())
            throw new UsernameNotFoundException("");

        Book book = new Book();
        book.setGbId(bookDTO.gbId());
        book.setIsbn13(bookDTO.isbn13());
        book.setTitle(bookDTO.title());
        book.setCoverUrl(bookDTO.coverUrl());
        book.setDescription(bookDTO.description());
        book.setGenres(bookDTO.genres());
        book.setPageCount(bookDTO.pageCount());
        book.setPublishedDate(bookDTO.publishedDate());
        book.setPublisher(bookDTO.publisher());

        List<Author> authorList = new ArrayList<>();
        for (String authorName : bookDTO.authors()) {
            Author author = new Author(authorName, book);
            authorList.add(author);
        }

        book.getAuthors().addAll(authorList);

        return bookRepository.save(book);
    }

    public void update(String gbId, UserBookUpdateDTO userBookUpdateDTO) {
//        Book book = bookRepository.findBookByGbId(gbId).orElseThrow(
//                () -> new EntityNotFoundException("Game with id " + gbId + " not found"));
//        book.setStatus(userBookUpdateDTO.status().orElse(book.getStatus()));
//        book.setUserRating(userBookUpdateDTO.userRating().orElse(book.getUserRating()));
//
//        if (userBookUpdateDTO.notes().isPresent()) {
//            userBookUpdateDTO.notes().ifPresent(notes -> {
//                notes.forEach(note -> {
//                    note.setBook(book);
//                    book.getNotes().add(note);
//                });
//            });
//        }

        System.out.println("Запись изменена");
//        bookRepository.save(book);
    }

    @Transactional
    public void delete(String gbId) {
        bookRepository.deleteBookByGbId(gbId);
    }

    public boolean isContains(String gbId) {
        return bookRepository.existsBookByGbId(gbId);
    }

    public Iterable<Book> getRecommendationByGenre(String genre) {
        if (genre == null || genre.isEmpty()) {
            throw new IllegalArgumentException("Genre cannot be null or empty");
        }

        return bookRepository.findBooksByGenreContaining(genre);
    }
}
