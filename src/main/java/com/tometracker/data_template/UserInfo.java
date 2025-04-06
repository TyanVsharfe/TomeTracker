package com.tometracker.data_template;

import lombok.Getter;

import java.util.List;

@Getter
public class UserInfo {
    String username;
    Long allBookQuantity;
    List <UserBookCountInfo> userBookQuantity;

    public UserInfo(String username, Long allBookQuantity, List<UserBookCountInfo> userBookQuantity) {
        this.username = username;
        this.allBookQuantity = allBookQuantity;
        this.userBookQuantity = userBookQuantity;
    }

    @Getter
    public static class UserBookCountInfo {
        String category;
        Long bookQuantity;

        public UserBookCountInfo(String category, Long bookQuantity) {
            this.category = category;
            this.bookQuantity = bookQuantity;
        }
    }
}


