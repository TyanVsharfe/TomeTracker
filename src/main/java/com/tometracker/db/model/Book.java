package com.tometracker.db.model;

import com.tometracker.dto.BookDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "books")
@Getter
public class Book {
    @Id
    private String gbId;
    private long isbn13;
    private String title;
    @Column(length = 512)
    private String coverUrl;

    public Book(BookDTO bookDTO) {
        this.gbId = bookDTO.gbId();
        this.isbn13 = bookDTO.isbn13();
        this.title = bookDTO.title();
        this.coverUrl = bookDTO.coverUrl();
    }

    public Book() {

    }
}
