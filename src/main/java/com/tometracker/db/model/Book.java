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
public class Book {
    @Id
    private String gbId;
    private long isbn13;
    private String title;
    @Getter
    @Setter
    private Enums.status status;
    @Getter
    @Setter
    private Double userRating;
    private String coverUrl;
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @Getter
    private List<Note> notes = new ArrayList<>();

    public Book(BookDTO bookDTO) {
        this.gbId = bookDTO.gbId();
        this.isbn13 = bookDTO.isbn13();
        this.title = bookDTO.title();
        this.coverUrl = bookDTO.coverUrl();
        this.status = Enums.status.None;
    }

    public Book() {

    }
}
