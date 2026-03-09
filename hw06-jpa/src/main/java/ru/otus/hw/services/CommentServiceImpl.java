package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private static final String COMMENT_NOT_FOUND_MESSAGE = "Comment with id %d not found";

    private static final String BOOK_NOT_FOUND_MESSAGE = "Book with id %d not found";

    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    private final CommentConverter commentConverter;

    @Override
    @Transactional(readOnly = true)
    public CommentDto findById(long id) {
        var comment = commentRepository.findById(id)
                .orElseThrow(entityNotFoundExceptionSupplier(COMMENT_NOT_FOUND_MESSAGE.formatted(id)));
        return commentConverter.toDto(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> findByBookId(long bookId) {
        var book = bookRepository.findById(bookId)
                .orElseThrow(entityNotFoundExceptionSupplier(BOOK_NOT_FOUND_MESSAGE.formatted(bookId)));
        return commentRepository.findByBook(book).stream()
                .map(commentConverter::toDto)
                .toList();
    }

    @Override
    @Transactional
    public CommentDto insert(String message, long bookId) {
        var book = bookRepository.findById(bookId)
                .orElseThrow(entityNotFoundExceptionSupplier(BOOK_NOT_FOUND_MESSAGE.formatted(bookId)));
        var createdAt = OffsetDateTime.now();
        var comment = commentRepository.save(new Comment(0, message, book, createdAt));
        return commentConverter.toDto(comment);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        var comment = commentRepository.findById(id)
                .orElseThrow(entityNotFoundExceptionSupplier(COMMENT_NOT_FOUND_MESSAGE.formatted(id)));
        commentRepository.delete(comment);
    }

    private Supplier<EntityNotFoundException> entityNotFoundExceptionSupplier(String message) {
        return () -> new EntityNotFoundException(message);
    }

}
