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
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({MockitoExtension.class})
class TestServiceImplTest {

    @Mock
    private IOService ioService;

    @Mock
    private QuestionDao questionDao;

    @InjectMocks
    private TestServiceImpl testService;

    @DisplayName("Should exec dependencies methods with expected frequency and with expected result")
    @Test
    void shouldExecDependenciesMethodsWithExpectedFrequencyAndResult() {
        var questionList = getTestQuestionList();
        var answerCount = getTestAnswerCount(questionList);
        var header = "Please answer the questions below%n";
        var student = getTestStudent();
        var studentAnswer = 3;
        var expectedTestResult = getTestResult(student, questionList);

        Mockito.when(questionDao.findAll()).thenReturn(questionList);
        Mockito.doNothing().when(ioService).printLine(Mockito.anyString());
        Mockito.doNothing().when(ioService).printFormattedLine(Mockito.anyString());
        Mockito.doNothing().when(ioService)
                .printFormattedLine(Mockito.anyString(), Mockito.anyInt(), Mockito.anyString());
        Mockito.doNothing().when(ioService).printFormattedLine(Mockito.anyString(), Mockito.anyString());
        Mockito.when(ioService.readIntForRangeWithPrompt(
                        Mockito.anyInt(),
                        Mockito.anyInt(),
                        Mockito.anyString(),
                        Mockito.anyString()
                ))
                .thenReturn(studentAnswer);

        var actualTestResult = testService.executeTestFor(student);

        Mockito.verify(questionDao, Mockito.times(1)).findAll();
        // вызов метода печати пустой строки
        Mockito.verify(ioService, Mockito.times(1)).printLine(Mockito.anyString());
        // вызов метода печати заголовка
        Mockito.verify(ioService, Mockito.times(1)).printFormattedLine(header);
        // вызов метода печати вопросов
        Mockito.verify(ioService, Mockito.times(questionList.size()))
                .printFormattedLine(Mockito.anyString(), Mockito.anyString());
        // вызов метода печати ответов
        Mockito.verify(ioService, Mockito.times(answerCount))
                .printFormattedLine(Mockito.anyString(), Mockito.anyInt(), Mockito.anyString());
        // проверка результата
        assertThat(actualTestResult)
                .withFailMessage("Результат прогона тестовых вопросов не соответствует ожидаемому")
                .isEqualTo(expectedTestResult);
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

    private static Student getTestStudent() {
        return new Student("Иван", "Иванов");
    }

    private static int getTestAnswerCount(List<Question> questions) {
        return questions.stream()
                .mapToInt(question -> question.answers().size())
                .sum();
    }

    private static TestResult getTestResult(Student student, List<Question> questions) {
        var expectedTestResult = new TestResult(student);
        expectedTestResult.applyAnswer(questions.get(0), false);
        expectedTestResult.applyAnswer(questions.get(1), true);
        return expectedTestResult;
    }

}