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

    private static final String INPUT_PROMPT = "Input a digit from range [%d; %d]";

    private static final String INPUT_ERROR_MESSAGE = "Incorrect answer format. Please, try again";

    private static final String ANSWER_FORMAT = "(%d) %s";

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
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
        int i = 0;
        for (var answer : question.answers()) {
            i++;
            ioService.printFormattedLine(ANSWER_FORMAT, i, answer.text());
        }
    }

    private int readStudentAnswer(int minAnswerNumber, int maxAnswerNumber) {
        return ioService.readIntForRangeWithPrompt(
                minAnswerNumber,
                maxAnswerNumber,
                INPUT_PROMPT.formatted(minAnswerNumber, maxAnswerNumber),
                INPUT_ERROR_MESSAGE
        );
    }


}
