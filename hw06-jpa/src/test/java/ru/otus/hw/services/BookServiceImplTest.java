package ru.otus.hw.services;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Сервис для работы с книгами")
@SpringBootTest
class BookServiceImplTest {

    @Autowired
    private BookServiceImpl bookService;

    @Autowired
    private EntityManager entityManager;

    @DisplayName("поиск книги не должен вызывать LazyInitializationException")
    @Test
    void findById_shouldNotThrowLazyInitializationException() {
        var book = bookService.findById(1L);

        // имитируем выход из транзакции — закрываем сессию
        entityManager.clear();

        // проверяем доступ к ленивым связям
        assertDoesNotThrow(() -> {
            var author = book.getAuthor();
            assertNotNull(author, "Автор не должен быть null после инициализации");
            var genre = book.getGenre();
            assertNotNull(genre, "Жанр не должен быть null после инициализации");
            System.out.println("Автор: " + author.getFullName() + ", жанр: " + genre.getName());
        }, "Доступ к author/genre не должен вызывать LazyInitializationException");
    }

    @DisplayName("запрос всех книг не должен вызывать LazyInitializationException")
    @Test
    void findAll_shouldNotThrowLazyInitializationException() {
        var books = bookService.findAll();

        // имитируем выход из транзакции — закрываем сессию
        entityManager.clear();

        // проверяем доступ к ленивой связи
        assertDoesNotThrow(() -> {
            for (var book : books) {
                var author = book.getAuthor();
                assertNotNull(author, "Автор не должен быть null после инициализации");
                var genre = book.getGenre();
                assertNotNull(genre, "Жанр не должен быть null после инициализации");
                System.out.println("Автор: " + author.getFullName() + ", жанр: " + genre.getName());
            }
        }, "Доступ к author/genre не должен вызывать LazyInitializationException");
    }

}