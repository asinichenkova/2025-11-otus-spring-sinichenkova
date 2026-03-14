package ru.otus.hw.services;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Сервис для работы с комментариями")
@SpringBootTest
class CommentServiceImplTest {

    @Autowired
    private CommentServiceImpl commentService;

    @Autowired
    private EntityManager entityManager;

    @DisplayName("поиск комментария по идентификатору не должен вызывать LazyInitializationException")
    @Test
    void findById() {
        var comment = commentService.findById(1L);

        // имитируем выход из транзакции — закрываем сессию
        entityManager.clear();

        // проверяем доступ к ленивым связям
        assertDoesNotThrow(() -> {
            var book = comment.getBook();
            assertNotNull(book, "Книга не должен быть null после инициализации");
            var author = book.getAuthor();
            assertNotNull(author, "Автор не должен быть null после инициализации");
            var genre = book.getGenre();
            assertNotNull(genre, "Жанр не должен быть null после инициализации");
            System.out.println("Комментарий: " + comment.getMessage() +
                            " к книге [автор: " + author.getFullName() +
                            ", жанр: " + genre.getName() + "]"
            );
        }, "Доступ к book/author/genre не должен вызывать LazyInitializationException");
    }

    @DisplayName("поиск комментария по книге не должен вызывать LazyInitializationException")
    @Test
    void findByBookId() {
        var comments = commentService.findByBookId(1L);

        // имитируем выход из транзакции — закрываем сессию
        entityManager.clear();

        // проверяем доступ к ленивым связям
        assertDoesNotThrow(() -> {
            for (var comment : comments) {
                var book = comment.getBook();
                assertNotNull(book, "Книга не должен быть null после инициализации");
                var author = book.getAuthor();
                assertNotNull(author, "Автор не должен быть null после инициализации");
                var genre = book.getGenre();
                assertNotNull(genre, "Жанр не должен быть null после инициализации");
                System.out.println("Комментарий: " + comment.getMessage() +
                        " к книге [автор: " + author.getFullName() +
                        ", жанр: " + genre.getName() + "]"
                );
            }
        }, "Доступ к book/author/genre не должен вызывать LazyInitializationException");
    }

}