package ru.otus.hw.repositories;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Репозиторий на основе Jpa для работы с книгами")
@DataJpaTest
@Import(JpaBookRepository.class)
class JpaBookRepositoryTest {

    private static final int EXPECTED_NUMBER_OF_BOOKS = 3;

    private static final long FIRST_BOOK_ID = 1L;

    private static final long HUNDREDTH_BOOK_ID = 100L;

    @Autowired
    private JpaBookRepository repositoryJpa;

    @Autowired
    private TestEntityManager testEntityManager;

    @DisplayName("должен загружать книгу по идентификатору")
    @Test
    void shouldReturnBookById() {
        var expectedBook = testEntityManager.find(Book.class, FIRST_BOOK_ID);
        var actualBook = repositoryJpa.findById(FIRST_BOOK_ID);
        assertThat(actualBook).isPresent().get().usingRecursiveComparison().isEqualTo(expectedBook);
    }

    @DisplayName("должен возвращать Optional.empty, если не удалось найти книгу по идентификатору")
    @Test
    void shouldReturnOptionalEmpty_whenBookByIdNotFound() {
        var actualBook = repositoryJpa.findById(HUNDREDTH_BOOK_ID);
        assertThat(actualBook).isNotPresent();
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnBookList() {
        var actualBooks = repositoryJpa.findAll();
        assertThat(actualBooks).isNotNull().hasSize(EXPECTED_NUMBER_OF_BOOKS);
    }

    @DisplayName("должен сохранять книгу")
    @Test
    void shouldSaveBook() {
        var authorId = 2L;
        var author = testEntityManager.find(Author.class, authorId);
        var genreId = 2L;
        var genre = testEntityManager.find(Genre.class, genreId);

        var book = new Book(0, "NewBookTitle", author, genre);
        var savedBook = repositoryJpa.save(book);
        var expectedBook = testEntityManager.find(Book.class, book.getId());
        assertThat(expectedBook).usingRecursiveComparison().isEqualTo(savedBook);
    }

    @DisplayName("должен удялать книгу")
    @Test
    void shouldDeleteBook() {
        repositoryJpa.deleteById(FIRST_BOOK_ID);
        assertThat(testEntityManager.find(Book.class, FIRST_BOOK_ID)).isNull();
    }

    @DisplayName("должен вызывать EntityNotFoundException, если книга с таким идентификатором не существует")
    @Test
    void shouldThrowEntityNotFoundException() {
        assertThrows(EntityNotFoundException.class, () -> repositoryJpa.deleteById(HUNDREDTH_BOOK_ID));
    }

}