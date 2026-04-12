package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jpa для работы с авторами")
@DataJpaTest
@Import(JpaAuthorRepository.class)
public class JpaAuthorRepositoryTest {

    private static final int EXPECTED_NUMBER_OF_AUTHORS = 3;

    private static final long FIRST_AUTHOR_ID = 1L;

    @Autowired
    private JpaAuthorRepository repositoryJpa;

    @Autowired
    private TestEntityManager testEntityManager;

    @DisplayName("должен загружать список всех авторов")
    @Test
    void shouldReturnAuthorList() {
        var actualAuthors = repositoryJpa.findAll();
        assertThat(actualAuthors).isNotNull().hasSize(EXPECTED_NUMBER_OF_AUTHORS);
    }

    @DisplayName("должен загружать автора по идентификатору")
    @Test
    void shouldReturnAuthorById() {
        var expectedAuthor = testEntityManager.find(Author.class, FIRST_AUTHOR_ID);
        var actualAuthor = repositoryJpa.findById(FIRST_AUTHOR_ID);
        assertThat(actualAuthor).isPresent().get().usingRecursiveComparison().isEqualTo(expectedAuthor);
    }

}