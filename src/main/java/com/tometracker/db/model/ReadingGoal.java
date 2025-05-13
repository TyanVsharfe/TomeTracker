package com.tometracker.db.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "reading_goals")
@Getter
@Setter
@NoArgsConstructor
public class ReadingGoal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private int targetBooks;

    @Column(nullable = false)
    private int currentBooks;

    @Column(nullable = false)
    @JsonDeserialize(using = InstantDeserializer.class)
    private Instant startDate;

    @Column(nullable = false)
    @JsonDeserialize(using = InstantDeserializer.class)
    private Instant endDate;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private Boolean completed = false;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GoalType goalType;

    public enum GoalType {
        YEARLY,
        MONTHLY,
        CUSTOM
    }

    public ReadingGoal(User user, String title, Integer targetBooks, Instant startDate, Instant endDate, GoalType goalType) {
        this.user = user;
        this.title = title;
        this.targetBooks = targetBooks;
        this.currentBooks = 0;
        this.startDate = startDate;
        this.endDate = endDate;
        this.goalType = goalType;
    }
}
