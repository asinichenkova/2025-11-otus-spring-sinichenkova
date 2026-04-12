package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Genre;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jpa для работы с жанрами")
@DataJpaTest
@Import(JpaGenreRepository.class)
public class JpaGenreRepositoryTest {

    private static final int EXPECTED_NUMBER_OF_GENRES = 3;

    private static final long FIRST_GENRE_ID = 1L;

    @Autowired
    private JpaGenreRepository repositoryJpa;

    @Autowired
    private TestEntityManager testEntityManager;

    @DisplayName("должен загружать список всех жанров")
    @Test
    void shouldReturnGenreList() {
        var actualGenres = repositoryJpa.findAll();
        assertThat(actualGenres).isNotNull().hasSize(EXPECTED_NUMBER_OF_GENRES);
    }

    @DisplayName("должен загружать жанр по идентификатору")
    @Test
    void shouldReturnGenreById() {
        var expectedGenre = testEntityManager.find(Genre.class, FIRST_GENRE_ID);
        var actualGenre = repositoryJpa.findById(FIRST_GENRE_ID);
        assertThat(actualGenre).isPresent().get().usingRecursiveComparison().isEqualTo(expectedGenre);
    }

}
