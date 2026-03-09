package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@AllArgsConstructor
public class CommentDto {

    private long id;

    private String message;

    private BookDto book;

    private OffsetDateTime createdAt;

}
