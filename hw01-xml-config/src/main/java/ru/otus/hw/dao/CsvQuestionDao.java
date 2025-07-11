package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {

    private static final int SKIP_LINES_COUNT = 1;

    private static final String QUESTION_READ_ERROR_MESSAGE = "Ошибка чтения файла с вопросами";

    private static final String FILENAME_IS_EMPTY_MESSAGE = "не задано имя файла";

    private static final String FILE_NOT_FOUND_MESSAGE = "файл %s не найден";

    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {
        List<Question> questions;
        try (var streamReader = new InputStreamReader(getFileFromResourceAsStream(fileNameProvider.getTestFileName())
                , StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(streamReader)
        ) {
            var csvToBean = new CsvToBeanBuilder<>(reader)
                    .withType(QuestionDto.class)
                    .withSeparator(';')
                    .withSkipLines(SKIP_LINES_COUNT)
                    .build()
                    .parse();
            questions = csvToBean.stream()
                    .map(obj -> (QuestionDto) obj)
                    .map(QuestionDto::toDomainObject)
                    .toList();
        } catch (IOException e) {
            throw new QuestionReadException(QUESTION_READ_ERROR_MESSAGE);
        }

        return questions;
    }

    private InputStream getFileFromResourceAsStream(String fileName) {
        if (fileName == null) {
            throw new QuestionReadException(QUESTION_READ_ERROR_MESSAGE + ": " + FILENAME_IS_EMPTY_MESSAGE);
        }

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);

        if (inputStream == null) {
            throw new QuestionReadException(QUESTION_READ_ERROR_MESSAGE + ": "
                    + FILE_NOT_FOUND_MESSAGE.formatted(fileName));
        } else {
            return inputStream;
        }

    }

}
