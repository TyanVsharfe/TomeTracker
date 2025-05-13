package com.tometracker.controller;

import com.tometracker.db.model.Book;
import com.tometracker.dto.BookDTO;
import com.tometracker.dto.UserBookUpdateDTO;
import com.tometracker.service.BookService;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping("")
    public void add(@RequestBody BookDTO bookDTO) {
        bookService.add(bookDTO);
    }

    @GetMapping("/{id}")
    public Optional<Book> get(@PathVariable("id") String id) {
        return bookService.get(id);
    }

    @GetMapping("/all")
    public Iterable<Book> getAll() {
        return bookService.getAll();
    }

    @PutMapping("")
    public void update(@PathVariable("id") String id, @RequestBody UserBookUpdateDTO bookDTO) {
        bookService.update(id, bookDTO);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable("id") String id) {
        bookService.delete(id);
    }

    @GetMapping("/check-entity/{id}")
    public boolean isContains(@PathVariable("id") String id) {
        return bookService.isContains(id);
    }

    @GetMapping("/recommendations")
    public Iterable<Book> getRecommendationByGenre(@RequestParam(value = "genre") String genre) {
        return bookService.getRecommendationByGenre(genre);
    }
}
