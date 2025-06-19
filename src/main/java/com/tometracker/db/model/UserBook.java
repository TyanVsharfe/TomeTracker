package com.tometracker.db.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.tometracker.data_template.Enums;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_books")
@Getter
@NoArgsConstructor
public class UserBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_id", referencedColumnName = "gbId", nullable = false)
    private Book book;

//    @OneToMany(mappedBy = "user_book", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonManagedReference
//    private List<Note> notes = new ArrayList<>();

    @Setter
    @Column(columnDefinition = "TEXT")
    private String review;

    @Setter
    private Enums.status status;
    @Setter
    private Double userRating;
    @Setter
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Setter
    @Column(name = "updated_at")
    private Instant updatedAt;

    public UserBook(User user, Book book) {
        this.user = user;
        this.book = book;
        this.status = Enums.status.None;
        this.createdAt = LocalDateTime.now().toInstant(ZoneOffset.UTC);
    }
}
