package com.tometracker.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
public class BookRestController {

    @PostMapping("")
    public void add() {

    }

    @GetMapping("{id}")
    public void get() {

    }

    @GetMapping("/all")
    public void getAll() {

    }

    @PutMapping("{id}")
    public void update() {

    }

    @DeleteMapping("{id}")
    public void delete() {

    }
}
