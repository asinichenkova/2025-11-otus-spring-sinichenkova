package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {

    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final BookConverter bookConverter;

    @Override
    @Transactional(readOnly = true)
    public BookDto findById(long id) {
        var book = bookRepository.findById(id)
                .orElseThrow(entityNotFoundExceptionSupplier("Book with id %d not found".formatted(id)));
        return bookConverter.toDto(book);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDto> findAll() {
        return bookRepository.findAll().stream()
                .map(bookConverter::toDto)
                .toList();
    }

    @Override
    @Transactional
    public BookDto insert(String title, long authorId, long genreId) {
        var author = authorRepository.findById(authorId)
                .orElseThrow(entityNotFoundExceptionSupplier("Author with id %d not found".formatted(authorId)));
        var genre = genreRepository.findById(genreId)
                .orElseThrow(entityNotFoundExceptionSupplier("Genre with id %d not found".formatted(genreId)));
        var book = bookRepository.save(new Book(0, title, author, genre));
        return bookConverter.toDto(book);
    }

    @Override
    @Transactional
    public BookDto update(long id, String title, long authorId, long genreId) {
        var author = authorRepository.findById(authorId)
                .orElseThrow(entityNotFoundExceptionSupplier("Author with id %d not found".formatted(authorId)));
        var genre = genreRepository.findById(genreId)
                .orElseThrow(entityNotFoundExceptionSupplier("Genre with id %d not found".formatted(genreId)));
        var book = bookRepository.save(new Book(id, title, author, genre));
        return bookConverter.toDto(book);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        bookRepository.deleteById(id);
    }

    private Supplier<EntityNotFoundException> entityNotFoundExceptionSupplier(String message) {
        return () -> new EntityNotFoundException(message);
    }

}
