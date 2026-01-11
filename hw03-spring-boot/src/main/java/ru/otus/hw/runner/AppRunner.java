package ru.otus.hw.runner;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import ru.otus.hw.service.TestRunnerService;

@Component
@RequiredArgsConstructor
public class AppRunner implements ApplicationRunner {

    private final TestRunnerService testRunnerService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        testRunnerService.run();
    }

}
