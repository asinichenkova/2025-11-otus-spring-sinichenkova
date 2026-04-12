package ru.otus.hw.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.models.Author;

@Component
public class AuthorConverter {

    public String authorToString(AuthorDto author) {
        return "Id: %d, FullName: %s".formatted(author.getId(), author.getFullName());
    }

    public AuthorDto toDto(Author entity) {
        return new AuthorDto(entity.getId(), entity.getFullName());
    }

}
