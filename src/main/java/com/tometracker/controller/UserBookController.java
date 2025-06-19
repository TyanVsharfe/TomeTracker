package com.tometracker.controller;

import com.tometracker.db.model.User;
import com.tometracker.db.model.UserBook;
import com.tometracker.dto.UserBookDTO;
import com.tometracker.dto.UserBookUpdateDTO;
import com.tometracker.dto.UserReviewsDTO;
import com.tometracker.service.UserBookService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("users/books")
public class UserBookController {
    private final UserBookService userBookService;

    public UserBookController(UserBookService userBookService) {
        this.userBookService = userBookService;
    }

    @PostMapping("/{id}")
    public void add(@PathVariable("id") String id, @AuthenticationPrincipal User user) {
        userBookService.add(id, user);
    }

    @GetMapping("/{id}")
    public Optional<UserBookDTO> get(@PathVariable("id") String id, @AuthenticationPrincipal User user) {
        return userBookService.get(id, user);
    }

    @GetMapping("/all")
    public Iterable<UserBook> getAll(@RequestParam(value = "status", required = false) String status, @AuthenticationPrincipal User user) {
        return userBookService.getAllBooks(status, user);
    }

    @GetMapping("/all-genres")
    public Iterable<String> getAllGenres(@AuthenticationPrincipal User user) {
        return userBookService.getAllGenre(user);
    }

    @GetMapping("/all-authors")
    public Iterable<String> getAllAuthors(@AuthenticationPrincipal User user) {
        return userBookService.getAllAuthors(user);
    }

    @PutMapping("{id}")
    public void update(@PathVariable("id") String id, @RequestBody UserBookUpdateDTO bookDTO, @AuthenticationPrincipal User user) {
        userBookService.update(id, bookDTO, user);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable("id") String id, @AuthenticationPrincipal User user) {
        userBookService.delete(id, user);
    }

    @GetMapping("/{gbId}/reviews")
    public List<UserReviewsDTO> getBookReviews(@PathVariable String gbId) {
        return userBookService.getBookReviews(gbId);
    }

    @GetMapping("/check-entity/{id}")
    public boolean isContains(@PathVariable("id") String id, @AuthenticationPrincipal User user) {
        return userBookService.isContains(id, user);
    }
}
