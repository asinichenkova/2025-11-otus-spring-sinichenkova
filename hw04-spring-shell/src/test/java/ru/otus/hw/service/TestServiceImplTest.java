package ru.otus.hw.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {TestServiceImpl.class})
class TestServiceImplTest {

    @MockitoBean
    private LocalizedIOService ioService;

    @MockitoBean
    private QuestionDao questionDao;

    @Autowired
    private TestServiceImpl testService;

    @DisplayName("Should exec dependencies methods with expected frequency and with expected result")
    @Test
    void shouldExecDependenciesMethodsWithExpectedFrequencyAndResult(

    ) {
        var questionList = getTestQuestionList();
        var answerCount = getTestAnswerCount(questionList);
        var headerMessageCode = "TestService.answer.the.questions";
        var inputPromptMessageCode = "TestService.input.a.digit.from.range";
        var incorrectAnswerFormatMessageCode = "TestService.incorrect.answer.format";
        var student = getTestStudent();
        var studentAnswer = 3;
        var expectedTestResult = getTestResult(student, questionList);

        doNothing().when(ioService).printLine(Mockito.anyString());
        doNothing().when(ioService).printLineLocalized(Mockito.anyString());
        doNothing().when(ioService).printFormattedLine(Mockito.anyString(), Mockito.anyString());
        when(questionDao.findAll()).thenReturn(questionList);
        when(ioService.getMessage(
                Mockito.eq(inputPromptMessageCode),
                Mockito.anyInt(),
                Mockito.anyInt()
        )).thenReturn("some text");
        when(ioService.getMessage(incorrectAnswerFormatMessageCode)).thenReturn("some error text");
        when(ioService.readIntForRangeWithPrompt(
                Mockito.anyInt(),
                Mockito.anyInt(),
                Mockito.anyString(),
                Mockito.anyString()
        ))
                .thenReturn(studentAnswer);

        var actualTestResult = testService.executeTestFor(student);

        verify(questionDao, Mockito.times(1)).findAll();
        // вызов метода печати пустой строки
        verify(ioService, Mockito.times(2)).printLine(Mockito.anyString());
        // вызов метода печати заголовка
        verify(ioService, Mockito.times(1)).printLineLocalized(headerMessageCode);
        // вызов метода печати вопросов
        verify(ioService, Mockito.times(questionList.size()))
                .printFormattedLine(Mockito.anyString(), Mockito.anyString());
        // вызов метода печати ответов
        verify(ioService, Mockito.times(answerCount))
                .printFormattedLine(Mockito.anyString(), Mockito.anyInt(), Mockito.anyString());
        // проверка результата
        assertThat(actualTestResult)
                .withFailMessage("Результат прогона тестовых вопросов не соответствует ожидаемому")
                .isEqualTo(expectedTestResult);
    }

    private List<Question> getTestQuestionList() {
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

    private Student getTestStudent() {
        return new Student("Иван", "Иванов");
    }

    private int getTestAnswerCount(List<Question> questions) {
        return questions.stream()
                .mapToInt(question -> question.answers().size())
                .sum();
    }

    private TestResult getTestResult(Student student, List<Question> questions) {
        var expectedTestResult = new TestResult(student);
        expectedTestResult.applyAnswer(questions.get(0), false);
        expectedTestResult.applyAnswer(questions.get(1), true);
        return expectedTestResult;
    }

}