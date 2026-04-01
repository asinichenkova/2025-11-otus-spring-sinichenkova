package ru.otus.hw.repositories;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Репозиторий на основе Jpa для работы с комментариями")
@DataJpaTest
@Import(JpaCommentRepository.class)
class JpaCommentRepositoryTest {

    private static final int EXPECTED_NUMBER_OF_BOOK_COMMENTS = 3;

    private static final long FIRST_COMMENT_ID = 1L;

    private static final long HUNDREDTH_COMMENT_ID = 100L;

    private static final long FIRST_BOOK_ID = 1L;

    private static final long HUNDREDTH_BOOK_ID = 100L;

    @Autowired
    private JpaCommentRepository repositoryJpa;

    @Autowired
    private TestEntityManager testEntityManager;

    @DisplayName("должен загружать комментарий по идентификатору")
    @Test
    void shouldReturnCommentById() {
        var expectedComment = testEntityManager.find(Comment.class, FIRST_COMMENT_ID);
        var actualComment = repositoryJpa.findById(FIRST_COMMENT_ID);
        assertThat(actualComment).isPresent().get().usingRecursiveComparison().isEqualTo(expectedComment);
    }

    @DisplayName("должен возвращать Optional.empty, если не удалось найти комментарий по идентификатору")
    @Test
    void shouldReturnOptionalEmpty_whenCommentByIdNotFound() {
        var actualComment = repositoryJpa.findById(HUNDREDTH_COMMENT_ID);
        assertThat(actualComment).isNotPresent();
    }

    @DisplayName("должен загружать комментарии по книге")
    @Test
    void shouldReturnCommentsByBook() {
        var book = testEntityManager.find(Book.class, FIRST_BOOK_ID);
        var bookComments = repositoryJpa.findByBook(book);
        assertThat(bookComments).isNotNull().hasSize(EXPECTED_NUMBER_OF_BOOK_COMMENTS);
    }

    @DisplayName("должен возвращать пустой список, если не удалось найти комментарии по книге")
    @Test
    void shouldReturnEmptyList_whenCommentsByBookNotFound() {
        var book = testEntityManager.find(Book.class, HUNDREDTH_BOOK_ID);
        var bookComments = repositoryJpa.findByBook(book);
        assertThat(bookComments).isEmpty();
    }

    @DisplayName("должен сохранять комментарий")
    @Test
    void shouldSaveComment() {
        var book = testEntityManager.find(Book.class, FIRST_BOOK_ID);
        var comment = new Comment(0, "NewBookComment", book, OffsetDateTime.now());
        var savedComment = repositoryJpa.save(comment);
        var expectedComment = testEntityManager.find(Comment.class, comment.getId());
        assertThat(expectedComment).usingRecursiveComparison().isEqualTo(savedComment);
    }

    @DisplayName("должен удялать комментарий")
    @Test
    void shouldDeleteComment() {
        repositoryJpa.deleteById(FIRST_COMMENT_ID);
        assertThat(testEntityManager.find(Comment.class, FIRST_COMMENT_ID)).isNull();
    }

    @DisplayName("должен вызывать EntityNotFoundException, если комментарий с таким идентификатором не существует")
    @Test
    void shouldThrowEntityNotFoundException() {
        assertThrows(EntityNotFoundException.class, () -> repositoryJpa.deleteById(HUNDREDTH_COMMENT_ID));
    }

}