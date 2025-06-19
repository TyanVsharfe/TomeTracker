package com.tometracker.db.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "book_statistics")
@Getter
@Setter
@NoArgsConstructor
public class BookStatistics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Instant date;

    private int booksAddedCount;

    @ElementCollection
    @CollectionTable(name = "genre_statistics",
            joinColumns = @JoinColumn(name = "statistics_id"))
    @MapKeyColumn(name = "genre")
    @Column(name = "count")
    private Map<String, Integer> genreStatistics = new HashMap<>();

    private int totalBooksInSystem;
    private int totalUsersWithBooks;

    public BookStatistics(Instant date) {
        this.date = date;
        this.booksAddedCount = 0;
    }

    public void incrementBooksAddedCount() {
        this.booksAddedCount++;
    }

    public void addGenre(String genre) {
        this.genreStatistics.put(genre,
                this.genreStatistics.getOrDefault(genre, 0) + 1);
    }
}