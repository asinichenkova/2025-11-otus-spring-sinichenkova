package ru.otus.hw.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;
import ru.otus.hw.service.LocalizedIOService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({MockitoExtension.class})
public class CsvQuestionDaoTest {

    @Mock
    private TestFileNameProvider fileNameProvider;

    @Mock
    private LocalizedIOService ioService;

    @InjectMocks
    private CsvQuestionDao dao;

    @DisplayName("Should throw the QuestionReadException")
    @ParameterizedTest(name = "''{0}'' in filename")
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    void shouldThrowQuestionReadExceptionBecauseOfBlankFilename(String filename) {
        Mockito.when(fileNameProvider.getTestFileName()).thenReturn(filename);

        Assertions.assertThrows(QuestionReadException.class, () -> dao.findAll());
    }

    @DisplayName("Should return expected question list")
    @Test
    void shouldReturnExpectedQuestionList() {
        var expectedQuestionList = getTestQuestionList();
        Mockito.when(fileNameProvider.getTestFileName()).thenReturn("questions.csv");

        var actualQuestionList = dao.findAll();

        assertThat(actualQuestionList.size())
                .withFailMessage("Количество прочитанных из файла вопросов не соответствует ожидаемому")
                .isEqualTo(expectedQuestionList.size());
        assertTrue(actualQuestionList.containsAll(expectedQuestionList)
                && expectedQuestionList.containsAll(actualQuestionList)
        );
    }

    private static List<Question> getTestQuestionList() {
        var answer1 = new Answer("A bedroom", false);
        var answer2 = new Answer("A mushroom", true);
        var answer3 = new Answer("A classroom", false);
        var question1 = new Question("What kind of room has no doors or windows?",
                List.of(answer1, answer2, answer3)
        );

        var answer5 = new Answer("A kitten", true);
        var answer6 = new Answer("A ghost", false);
        var answer7 = new Answer("A Bigfoot", false);
        var question2 = new Question("What has a head like a cat, feet like a cat, a tail like a cat, " +
                "but isn’t a cat?",
                List.of(answer5, answer6, answer7)
        );

        return List.of(question1, question2);
    }

}
