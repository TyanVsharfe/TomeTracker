package com.tometracker.dto;

import java.util.List;

public record BookDTO (String gbId, long isbn13, String title, String coverUrl, String description,
                       List<String> genres, int pageCount, String publishedDate, String publisher,
                       List<String> authors) {
}
