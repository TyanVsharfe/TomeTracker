package com.tometracker.data_template;

import lombok.Getter;

import java.util.List;

@Getter
public class UserInfo {
    String username;
    String subscription;
    Long allBookQuantity;
    List <UserBookCountInfo> userBookQuantity;
    List<GenreCountInfo> bookCountByGenre;

    public UserInfo(String username, String subscription, Long allBookQuantity,
                    List<UserBookCountInfo> userBookQuantity, List<GenreCountInfo> bookCountByGenre) {
        this.username = username;
        this.subscription = subscription;
        this.allBookQuantity = allBookQuantity;
        this.userBookQuantity = userBookQuantity;
        this.bookCountByGenre = bookCountByGenre;
    }

    @Getter
    public static class UserBookCountInfo {
        String category;
        Long count;

        public UserBookCountInfo(String category, Long bookQuantity) {
            this.category = category;
            this.count = bookQuantity;
        }
    }

    @Getter
    public static class GenreCountInfo {
        String genre;
        long count;

        public GenreCountInfo(String genre, long count) {
            this.genre = genre;
            this.count = count;
        }
    }
}


