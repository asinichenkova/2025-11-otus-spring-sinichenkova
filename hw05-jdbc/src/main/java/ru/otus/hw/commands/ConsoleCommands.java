package ru.otus.hw.commands;

import org.h2.tools.Console;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.sql.SQLException;

@ShellComponent
public class ConsoleCommands {

    @ShellMethod(value = "Run H2 Console", key = "h2c")
    public String runH2Console(
            @ShellOption(value = "args", defaultValue = ShellOption.NULL) String[] args
    ) throws SQLException {
        Console.main(args);
        return "H2 console URL is http://localhost:8082";
    }

}