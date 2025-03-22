package com.tometracker.controller;

import com.tometracker.db.model.Book;
import com.tometracker.db.model.UserBook;
import com.tometracker.dto.BookDTO;
import com.tometracker.dto.UserBookDTO;
import com.tometracker.dto.UserBookUpdateDTO;
import com.tometracker.service.UserBookService;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("users/books")
public class UserBookController {
    private final UserBookService userBookService;

    public UserBookController(UserBookService userBookService) {
        this.userBookService = userBookService;
    }

    @PostMapping("/{id}")
    public void add(@PathVariable("id") String id) {
        userBookService.add(id);
    }

    @GetMapping("/{id}")
    public Optional<UserBookDTO> get(@PathVariable("id") String id) {
        return userBookService.get(id);
    }

    @GetMapping("/all")
    public Iterable<Book> getAll(@RequestParam(value = "status", required = false) String status) {
        return userBookService.getAllBooks(status);
    }

    @PutMapping("{id}")
    public void update(@PathVariable("id") String id, @RequestBody UserBookUpdateDTO bookDTO) {
        userBookService.update(id, bookDTO);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable("id") String id) {
        userBookService.delete(id);
    }

    @GetMapping("/check-entity/{id}")
    public boolean isContains(@PathVariable("id") String id) {
        return userBookService.isContains(id);
    }
}
