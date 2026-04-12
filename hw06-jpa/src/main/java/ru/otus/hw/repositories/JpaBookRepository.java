package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH;
import static ru.otus.hw.constants.AppConstants.BOOK_ENTITY_GRAPH_NAME;

@Repository
@RequiredArgsConstructor
public class JpaBookRepository implements BookRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public Optional<Book> findById(long id) {
        var entityGraph = entityManager.getEntityGraph(BOOK_ENTITY_GRAPH_NAME);
        TypedQuery<Book> query = entityManager.createQuery(
                "select distinct b from Book b where b.id = :id", Book.class
        );
        query.setParameter("id", id);
        query.setHint(FETCH.getKey(), entityGraph);
        try {
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Book> findAll() {
        var entityGraph = entityManager.getEntityGraph(BOOK_ENTITY_GRAPH_NAME);
        TypedQuery<Book> query = entityManager.createQuery("select distinct b from Book b", Book.class);
        query.setHint(FETCH.getKey(), entityGraph);
        return query.getResultList();
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            entityManager.persist(book);
            return book;
        }
        return entityManager.merge(book);
    }

    @Override
    public void deleteById(long id) {
        var book = entityManager.getReference(Book.class, id);
        entityManager.remove(book);
    }

}
