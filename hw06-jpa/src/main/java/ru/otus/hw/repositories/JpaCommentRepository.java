package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH;
import static ru.otus.hw.constants.AppConstants.COMMENT_ENTITY_GRAPH_NAME;

@Repository
@RequiredArgsConstructor
public class JpaCommentRepository implements CommentRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public Optional<Comment> findById(long id) {
        var entityGraph = entityManager.getEntityGraph(COMMENT_ENTITY_GRAPH_NAME);
        TypedQuery<Comment> query = entityManager.createQuery(
                "select distinct c from Comment c where c.id = :id", Comment.class
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
    public List<Comment> findByBook(Book book) {
        var entityGraph = entityManager.getEntityGraph(COMMENT_ENTITY_GRAPH_NAME);
        TypedQuery<Comment> query = entityManager.createQuery(
                "select distinct c from Comment c where c.book = :book", Comment.class
        );
        query.setParameter("book", book);
        query.setHint(FETCH.getKey(), entityGraph);
        return query.getResultList();
    }

    @Override
    public Comment save(Comment comment) {
        if (comment.getId() == 0) {
            entityManager.persist(comment);
            return comment;
        }
        return entityManager.merge(comment);
    }

    @Override
    public void delete(Comment comment) {
        entityManager.remove(entityManager.contains(comment) ? comment : entityManager.merge(comment));
    }

}
