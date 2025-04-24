package com.tometracker.db.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.tometracker.data_template.Enums;
import com.tometracker.dto.BookDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "books")
@Getter
public class Book {
    @Id
    private String gbId;
    private long isbn13;
    private String title;
    @Lob
    private String description;
    @ElementCollection
    private List<String> genres;
    private Enums.maturity maturityRating;
    @Column(length = 512)
    private String coverUrl;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private final List<Author> authors = new ArrayList<>();

    public Book(BookDTO bookDTO) {
        this.gbId = bookDTO.gbId();
        this.isbn13 = bookDTO.isbn13();
        this.title = bookDTO.title();
        this.coverUrl = bookDTO.coverUrl();
        this.description = bookDTO.description();
        this.genres = bookDTO.genres();
    }

    public Book() {

    }
}
