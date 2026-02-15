package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    @Override
    public Optional<Book> findById(long id) {
        Map<String, Long> params = Map.of("id", id);
        try {
            var result = namedParameterJdbcOperations.queryForObject(
                    "select b.id, b.title, b.author_id, b.genre_id, a.full_name as author_name, g.name as genre_name " +
                            "from books b " +
                            "left join authors a on a.id = b.author_id " +
                            "left join genres g on g.id = b.genre_id " +
                            "where b.id = :id", params, new BookRowMapper()
            );
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Book> findAll() {
        return namedParameterJdbcOperations.query(
                "select b.id, b.title, b.author_id, b.genre_id, a.full_name as author_name, g.name as genre_name " +
                        "from books b " +
                        "left join authors a on a.id = b.author_id " +
                        "left join genres g on g.id = b.genre_id",
                new BookRowMapper());
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        Map<String, Long> params = Map.of("id", id);
        namedParameterJdbcOperations.update("delete from books where id = :id", params);
    }

    private Book insert(Book book) {
        var keyHolder = new GeneratedKeyHolder();

        var title = book.getTitle();
        var authorId = book.getAuthor() != null ? book.getAuthor().getId() : null;
        var genreId = book.getGenre() != null ? book.getGenre().getId() : null;
        var params = new MapSqlParameterSource(Map.of("title", title, "author_id", authorId, "genre_id", genreId));
        namedParameterJdbcOperations.update(
                "insert into books (title, author_id, genre_id) values (:title, :author_id, :genre_id)",
                params, keyHolder, new String[]{"id"}
        );

        // noinspection DataFlowIssue
        book.setId(keyHolder.getKeyAs(Long.class));
        return book;
    }

    private Book update(Book book) {
        long id = book.getId();
        String title = book.getTitle();
        var authorId = book.getAuthor() != null ? book.getAuthor().getId() : null;
        var genreId = book.getGenre() != null ? book.getGenre().getId() : null;
        Map<String, Object> params = Map.of("id", id, "title", title, "author_id", authorId, "genre_id", genreId);
        var count = namedParameterJdbcOperations.update(
                "update books set title = :title, author_id = :author_id, genre_id = :genre_id where id = :id", params
        );
        if (count == 0) {
            throw new EntityNotFoundException("No records found to update");
        }

        return book;
    }

    private static class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            long id = resultSet.getLong("id");
            String title = resultSet.getString("title");
            Author author = new Author(resultSet.getLong("author_id"), resultSet.getString("author_name"));
            Genre genre = new Genre(resultSet.getLong("genre_id"), resultSet.getString("genre_name"));

            return new Book(id, title, author, genre);
        }

    }

}
