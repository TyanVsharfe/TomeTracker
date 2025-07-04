package com.tometracker.dto;

import com.tometracker.data_template.Enums;
import com.tometracker.db.model.Note;

import java.util.List;
import java.util.Optional;

public record UserBookUpdateDTO(Optional<Enums.status> status, Optional<Double> userRating, Optional<String> review, Optional<List<Note>> notes){
}
