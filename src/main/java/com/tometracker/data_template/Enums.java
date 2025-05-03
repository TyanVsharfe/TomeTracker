package com.tometracker.data_template;

public class Enums {
    public enum status {
        Completed,
        Reading,
        Planned,
        Abandoned,
        None
    }

    public enum subscription {
        Free,
        Tracker
    }

    public enum maturity {
        Mature,
        NotMature
    }

    public enum AchievementCategory {
        TOTAL_BOOKS_READ,
        GENRE_SPECIFIC,
        AUTHOR_SPECIFIC,
        PAGES_READ,
        SERIES_COMPLETED,
        READING_STREAK,
        REVIEWS_WRITTEN,
        PUBLISHER_SPECIFIC
    }

    public enum BooksGenres {
        BUSINESS_AND_ECONOMICS("BUSINESS & ECONOMICS");
        private String genre;

        BooksGenres(String genre) {
            this.genre = genre;
        }
    }
}
