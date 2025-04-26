package com.tometracker.db.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "authors")
@Getter
public class Author {
    @Id
    @GeneratedValue
    private Long id;
    private String name;

    @ManyToOne
    @JoinColumn(name = "book_id", referencedColumnName = "gbId")
    @JsonBackReference
    @Setter
    private Book book;

    public Author(String name, Book book) {
        this.name = name;
        this.book = book;
    }

    public Author() {

    }
}
