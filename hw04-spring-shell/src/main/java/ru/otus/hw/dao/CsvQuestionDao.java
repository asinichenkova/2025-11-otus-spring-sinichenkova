package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;
import ru.otus.hw.service.LocalizedIOService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class CsvQuestionDao implements QuestionDao {

    private static final int SKIP_LINES_COUNT = 1;

    private final LocalizedIOService ioService;

    private final String questionReadingErrorMessage;

    private final TestFileNameProvider appProperties;

    public CsvQuestionDao(LocalizedIOService ioService, TestFileNameProvider appProperties) {
        this.ioService = ioService;
        this.appProperties = appProperties;
        this.questionReadingErrorMessage = ioService.getMessage("CsvQuestionDao.question.read.error.message");
    }

    @Override
    public List<Question> findAll() {
        List<Question> questions;
        var fileName = appProperties.getTestFileName();
        try (var inputStream = getFileFromResourceAsStream(fileName);
             var streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             var reader = new BufferedReader(streamReader)
        ) {
            var csvToBean = new CsvToBeanBuilder<>(reader)
                    .withType(QuestionDto.class)
                    .withSeparator(';')
                    .withSkipLines(SKIP_LINES_COUNT)
                    .build()
                    .parse();
            questions = csvToBean.stream()
                    .map(QuestionDto.class::cast)
                    .map(QuestionDto::toDomainObject)
                    .toList();
        } catch (IOException e) {
            throw new QuestionReadException(questionReadingErrorMessage);
        }

        return questions;
    }

    private InputStream getFileFromResourceAsStream(String fileName) {
        if (!StringUtils.hasText(fileName)) {
            throw new QuestionReadException(questionReadingErrorMessage + ": "
                    + ioService.getMessage("CsvQuestionDao.filename.is.empty"));
        }

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);

        if (inputStream == null) {
            throw new QuestionReadException(questionReadingErrorMessage + ": "
                    + ioService.getMessage("CsvQuestionDao.file.not.found", fileName)
            );
        } else {
            return inputStream;
        }
    }

}
