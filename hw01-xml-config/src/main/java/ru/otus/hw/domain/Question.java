package ru.otus.hw.domain;

import java.util.List;

public record Question(String text, List<Answer> answers) {

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(text + "\n");
        var i = 0;
        for (var answer : answers) {
            i++;
            var prefix = "(" + i + ") ";
            result.append(prefix).append(answer.text()).append("\n");
        }
        return result.toString().trim();
    }

}
