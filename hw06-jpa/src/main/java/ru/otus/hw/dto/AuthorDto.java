package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthorDto {

    private long id;

    private String fullName;

}