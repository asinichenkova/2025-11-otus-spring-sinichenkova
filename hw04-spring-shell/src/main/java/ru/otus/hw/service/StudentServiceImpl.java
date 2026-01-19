package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.domain.Student;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private Student currentStudent;

    private final LocalizedIOService ioService;

    @Override
    public Student determineCurrentStudent() {
        var firstName = ioService.readStringWithPromptLocalized("StudentService.input.first.name");
        var lastName = ioService.readStringWithPromptLocalized("StudentService.input.last.name");
        currentStudent = new Student(firstName, lastName);
        return currentStudent;
    }

    @Override
    public boolean isStudentDefined() {
        return nonNull(currentStudent);
    }

    @Override
    public Student getCurrentStudent() {
        return currentStudent;
    }

}
