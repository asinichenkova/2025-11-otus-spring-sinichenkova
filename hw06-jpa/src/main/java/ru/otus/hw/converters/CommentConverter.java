package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Comment;

import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Component
public class CommentConverter {

    private final BookConverter bookConverter;

    public String commentToString(CommentDto comment) {
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssZ");

        return "Id: %d, message: %s, book: [%s], created at: %s".formatted(
                comment.getId(),
                comment.getMessage(),
                bookConverter.bookToString(comment.getBook()),
                comment.getCreatedAt().format(formatter)
        );
    }

    public CommentDto toDto(Comment entity) {
        return new CommentDto(
                entity.getId(),
                entity.getMessage(),
                bookConverter.toDto(entity.getBook()),
                entity.getCreatedAt()
        );
    }

}
