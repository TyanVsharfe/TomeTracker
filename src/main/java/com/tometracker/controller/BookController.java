package com.tometracker.controller;

import com.tometracker.dto.BookDTO;
import com.tometracker.dto.BookUpdateDTO;
import com.tometracker.service.BookService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.web.bind.annotation.*;

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
    public void get(@PathVariable("id") String id) {
        bookService.get(id);
    }

    @GetMapping("/all")
    public void getAll() {
        bookService.getAll();
    }

    @PutMapping("{id}")
    public void update(@PathVariable("id") String id, BookUpdateDTO bookDTO) {
        bookService.update(id, bookDTO);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable("id") String id) {
        bookService.delete(id);
    }
}
