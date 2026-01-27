package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private static final String ANSWER_FORMAT = "(%d) %s";

    private final LocalizedIOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printLineLocalized("TestService.answer.the.questions");
        ioService.printLine("");

        var questions = questionDao.findAll();
        var testResult = new TestResult(student);

        for (var question : questions) {
            if (!StringUtils.hasText(question.text()) || question.answers() == null) {
                continue;
            }
            printQuestionWithAnswers(question);
            var minAnswerNumber = 1;
            var maxAnswerNumber = question.answers().size();
            var studentAnswer = readStudentAnswer(minAnswerNumber, maxAnswerNumber);
            var isAnswerValid = question.answers().get(studentAnswer - 1).isCorrect();
            testResult.applyAnswer(question, isAnswerValid);
        }
        return testResult;
    }

    private void printQuestionWithAnswers(Question question) {
        ioService.printFormattedLine("%s", question.text());
        for (int i = 0; i < question.answers().size(); i++) {
            var answer = question.answers().get(i);
            ioService.printFormattedLine(ANSWER_FORMAT, i + 1, answer.text());
        }
    }

    private int readStudentAnswer(int minAnswerNumber, int maxAnswerNumber) {
        var inputPrompt = ioService.getMessage(
                "TestService.input.a.digit.from.range",
                minAnswerNumber,
                maxAnswerNumber
        );
        var inputErrorMessage = ioService.getMessage("TestService.incorrect.answer.format");

        return ioService.readIntForRangeWithPrompt(
                minAnswerNumber,
                maxAnswerNumber,
                inputPrompt,
                inputErrorMessage
        );
    }

}
