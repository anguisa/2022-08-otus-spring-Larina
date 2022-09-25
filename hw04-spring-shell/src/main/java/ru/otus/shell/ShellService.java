package ru.otus.shell;

import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;
import ru.otus.domain.Person;
import ru.otus.domain.Score;
import ru.otus.service.LocalizationService;
import ru.otus.service.TestingService;

@ShellComponent
public class ShellService {

    private final LocalizationService localizationService;
    private final TestingService testingService;
    private Person person;

    public ShellService(LocalizationService localizationService,
                        TestingService testingService) {
        this.localizationService = localizationService;
        this.testingService = testingService;
    }

    @ShellMethod(value = "Login", key = {"l", "login"})
    public String login(@ShellOption(help = "Enter first name", defaultValue = "Anonymous") String firstName,
                        @ShellOption(help = "Enter last name", defaultValue = "") String lastName) {
        this.person = new Person(firstName, lastName);
        return localizationService.localizeMessage("shell.welcome", this.person.getFirstName(), this.person.getLastName());
    }

    @ShellMethod(value = "Testing", key = {"t", "test", "testing"})
    @ShellMethodAvailability(value = "isTestCommandAvailable")
    public String test() {
        Score score = testingService.test(person);
        return localizationService.localizeMessage("shell.goodbye");
    }

    private Availability isTestCommandAvailable() {
        return person == null? Availability.unavailable(localizationService.localizeMessage("shell.login.error")): Availability.available();
    }
}
