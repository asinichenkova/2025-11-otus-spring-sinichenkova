package ru.otus.hw.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

@ExtendWith({MockitoExtension.class})
class TestServiceImplTest {

    @Mock
    private IOService ioService;

    @Mock
    private QuestionDao questionDao;

    @InjectMocks
    private TestServiceImpl testService;

    @DisplayName("Should exec dependencies methods with expected frequency")
    @Test
    void shouldExecDependenciesMethodsWithExpectedFrequency() {
        var questionList = getTestQuestionList();
        var header = "Please answer the questions below%n";
        Mockito.when(questionDao.findAll()).thenReturn(questionList);
        Mockito.doNothing().when(ioService).printLine(Mockito.anyString());
        Mockito.doNothing().when(ioService).printFormattedLine(Mockito.anyString());

        testService.executeTest();

        Mockito.verify(questionDao, Mockito.times(1)).findAll();
        Mockito.verify(ioService, Mockito.times(1)).printLine(Mockito.anyString());
        Mockito.verify(ioService, Mockito.times(1)).printFormattedLine(header);
        Mockito.verify(ioService, Mockito.times(questionList.size())).printFormattedLine(Mockito.anyString(),
                Mockito.any());
    }

    private static List<Question> getTestQuestionList() {
        var answer1 = new Answer("Somebody", false);
        var answer2 = new Answer("Nobody", false);
        var answer3 = new Answer("Unknown", false);
        var answer4 = new Answer("Anastasiya", true);
        var question1 = new Question("What is my name?", List.of(answer1, answer2, answer3, answer4));

        var answer5 = new Answer("18", false);
        var answer6 = new Answer("25", false);
        var answer7 = new Answer("39", true);
        var question2 = new Question("What is my age?", List.of(answer5, answer6, answer7));

        return List.of(question1, question2);
    }

}