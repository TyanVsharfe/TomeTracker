package com.tometracker.db.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.tometracker.data_template.Enums;
import com.tometracker.dto.BookDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "books")
@Getter
@Setter
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
    private int pageCount;
    private String publishedDate;
    private String publisher;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private final List<Author> authors = new ArrayList<>();

    public Book(BookDTO bookDTO, List<Author> authors) {
        this.gbId = bookDTO.gbId();
        this.isbn13 = bookDTO.isbn13();
        this.title = bookDTO.title();
        this.coverUrl = bookDTO.coverUrl();
        this.description = bookDTO.description();
        this.genres = bookDTO.genres();
        this.pageCount = bookDTO.pageCount();
        this.publishedDate = bookDTO.publishedDate();
        this.publisher = bookDTO.publisher();
        this.authors.addAll(authors);
    }

    public Book() {

    }
}
