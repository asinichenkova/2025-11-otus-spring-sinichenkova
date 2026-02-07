package ru.otus.hw.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import ru.otus.hw.service.LocalizedIOService;
import ru.otus.hw.service.StudentService;
import ru.otus.hw.service.TestRunnerService;

@ShellComponent(value = "Testing Application Commands")
@RequiredArgsConstructor
public class TestingApplicationCommands {

    private final TestRunnerService testRunnerService;

    private final StudentService studentService;

    private final LocalizedIOService ioService;

    @ShellMethod(value = "Start testing", key = {"s", "start"})
    @ShellMethodAvailability(value = "isUserDefined")
    public void start() {
        testRunnerService.run();
    }

    @ShellMethod(value = "Read student name", key = {"n", "name"})
    public String readStudentName() {
        var student = studentService.determineCurrentStudent();
        return ioService.getMessage("TestingApplicationCommands.current.user.is", student.getFullName());
    }

    private Availability isUserDefined() {
        return studentService.isStudentDefined()
                ? Availability.available()
                : Availability.unavailable(
                    ioService.getMessage("TestingApplicationCommands.input.your.full.name.first")
                );
    }

}
