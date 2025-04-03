package com.tometracker.dto;

import com.tometracker.data_template.Enums;
import com.tometracker.db.model.Book;
import com.tometracker.db.model.Note;

import java.util.List;
import java.util.Optional;

public record UserBookDTO(Book book, Enums.status status, Double userRating, String review, List<Note> notes){
}