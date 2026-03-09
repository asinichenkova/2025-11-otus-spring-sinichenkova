package ru.otus.hw.services;

import ru.otus.hw.dto.CommentDto;

import java.util.List;

public interface CommentService {

    CommentDto findById(long id);

    List<CommentDto> findByBookId(long bookId);

    CommentDto insert(String message, long bookId);

    void deleteById(long id);

}
